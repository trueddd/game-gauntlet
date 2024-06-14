package com.github.trueddd.ui.rules

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Rules(
    modifier: Modifier = Modifier,
) {
    val ruleItems = remember { listOf(MainInfo, HowToMakeMove, WhatGamesToPlay, WhatToReroll, HowToChooseDifficulty) }
    val pagerState = rememberPagerState(pageCount = { ruleItems.size })
    VerticalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        ruleItems.getOrNull(page)?.let {
            RuleItem(
                item = it,
                makePaddingOnTop = page == 0,
                makePaddingOnBottom = page == ruleItems.size - 1
            )
        }
    }
}

@Composable
private fun BasicText(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        fontSize = 36.sp,
        lineHeight = 48.sp,
    )
}

@Composable
private fun OrderedList(
    list: Content.OrderedList,
) {
    list.contentList.forEachIndexed { index, content ->
        BasicText(
            text = "${index + 1}. ${content.text}",
        )
    }
}

@Composable
private fun UnorderedList(
    list: Content.UnorderedList,
) {
    list.contentList.forEach { content ->
        BasicText(
            text = "- ${content.text}",
        )
    }
}

@Composable
private fun RuleItem(
    item: RuleItem,
    makePaddingOnTop: Boolean,
    makePaddingOnBottom: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(2f))
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .let {
                    var tempModifier = it
                    if (makePaddingOnTop) {
                        tempModifier = tempModifier.padding(top = 24.dp)
                    }
                    if (makePaddingOnBottom) {
                        tempModifier = tempModifier.padding(bottom = 24.dp)
                    }
                    tempModifier
                }
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                text = item.header,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 48.sp,
            )
            when (item.body) {
                is Content.Paragraph -> BasicText(item.body.text)
                is Content.OrderedList -> OrderedList(item.body)
                is Content.UnorderedList -> UnorderedList(item.body)
            }
        }
    }
}
