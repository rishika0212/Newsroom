package com.typhon.newsroom.presentation.news_screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.newsroom.presentation.component.RetryContent
import com.example.newsroom.presentation.component.SearchAppBar
import com.typhon.newsroom.domain.model.Article
import com.typhon.newsroom.presentation.component.BottomSheetContent
import com.typhon.newsroom.presentation.component.CategoryTabRow
import com.typhon.newsroom.presentation.component.NewsArticleCard
import com.typhon.newsroom.presentation.component.NewsScreenTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class)
@Composable
fun NewsScreen(
    state: NewsScreenState,
    onEvent:(NewsScreenEvent)->Unit,
    onReadFullStoryButtonClicked:(String)->Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pagerState= rememberPagerState{7}
    val coroutineScope= rememberCoroutineScope()
    val categories= listOf(
        "General","Business","Health","Science","Sports","Technology","Entertainment"
    )

    val focusRequester= remember{ FocusRequester()}
    val focusManager= LocalFocusManager.current
    val keyboardController= LocalSoftwareKeyboardController.current

    LaunchedEffect(key1=pagerState){
        snapshotFlow { pagerState.currentPage }.collect{page->
            onEvent(NewsScreenEvent.OnCategoryChanged(category=categories[page]))
        }
    }
    LaunchedEffect(key1=Unit) {
        if(state.searchQuery.isNotEmpty()){
            onEvent(NewsScreenEvent.OnSearchQueryChanged(searchQuery = state.searchQuery ))
        }
    }

    val sheetState= rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var shouldBottomSheetShow by remember{ mutableStateOf(false)    }

    if(shouldBottomSheetShow){
        ModalBottomSheet(
            onDismissRequest = {
                shouldBottomSheetShow=false
                               },
            sheetState = sheetState,
            content = {
                state.selectedArticle?.let{
                    BottomSheetContent(
                        article = it,
                        onReadFullStoryButtonClicked = {
                            onReadFullStoryButtonClicked(it.url)
                            coroutineScope.launch{sheetState.hide()}.invokeOnCompletion {
                                if(!sheetState.isVisible) shouldBottomSheetShow=false
                             }
                        }
                    )
                }
            }
        )
    }

    Column(modifier=Modifier.fillMaxSize()) {
        Crossfade(targetState = state.isSearchBarVisible) { isVisible->
            if(isVisible){
                Column {
                    SearchAppBar(
                        modifier=Modifier.focusRequester(focusRequester),
                        value =state.searchQuery ,
                        onValueChange = {newValue->
                                             onEvent(NewsScreenEvent.OnSearchQueryChanged(newValue))

                        },
                        onCloseIconClicked = { onEvent(NewsScreenEvent.OnCloseIconClicked) },
                        onSearchIconClicked = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    )
                    NewsArticlesList(
                        state = state,
                        onCardClicked = {article->
                            shouldBottomSheetShow=true
                            onEvent(NewsScreenEvent.OnNewsCardClicked(article=article))
                        },
                        onRetry = {
                            onEvent(NewsScreenEvent.OnSearchQueryChanged(state.searchQuery))
                        }
                    )
                }
            }
            else{
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        NewsScreenTopBar(
                            scrollBehavior = scrollBehavior,
                            onSearchIconClicked = {
                                onEvent(NewsScreenEvent.OnSearchIconClicked)
                                coroutineScope.launch {
                                    delay(500)
                                    focusRequester.requestFocus()
                                }

                            }
                        )
                    }

                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        CategoryTabRow(
                            pagerState = pagerState,
                            categories = categories,
                            onTabSelected ={ index->
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )
                        HorizontalPager(
                            beyondBoundsPageCount = 1,
                            state = pagerState
                        ) {
                            NewsArticlesList(
                                state = state,
                                onCardClicked = {article->
                                    shouldBottomSheetShow=true
                                    onEvent(NewsScreenEvent.OnNewsCardClicked(article=article))
                                },
                                onRetry = {
                                    onEvent(NewsScreenEvent.OnCategoryChanged(state.category))
                                }
                            )
                        }
                    }
                }
            }

        }

    }
}
@Composable
fun NewsArticlesList(
    state:NewsScreenState,
    onCardClicked:(Article)->Unit,
    onRetry:()-> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(state.articles) { article ->
            NewsArticleCard(
                article = article,
                onCardClicked = onCardClicked
            )
        }
    }
    Box(
        modifier=Modifier.fillMaxSize(),
        contentAlignment= Alignment.Center
    ){
        if(state.isLoading){
            CircularProgressIndicator()
        }
        if(state.error!=null){
            RetryContent(
                error = state.error ,
                onRetry = onRetry
            )
        }
    }
}











