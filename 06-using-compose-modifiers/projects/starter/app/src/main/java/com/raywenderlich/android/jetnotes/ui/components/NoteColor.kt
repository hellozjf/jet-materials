package com.raywenderlich.android.jetnotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NoteColor(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp,
    border: Dp
) {
    Box(
        modifier = modifier
            // 然后设置内部大小
            .size(size)
            // 这里设置 padding 的话，就是设置内边距了
            // 把画笔改成圆形画笔
            .clip(CircleShape)
            // 画一个红色圈上去
            .background(color)
            // 画边框
            .border(
                // 边框线宽 2dp，黑色
                BorderStroke(
                    border,
                    SolidColor(Color.Black)
                ),
                // 圆形
                CircleShape
            )
    )
}

@Preview
@Composable
fun NoteColorPreview() {
    NoteColor(
        color = Color.Red,
        size = 40.dp,
        border = 2.dp
    )
}