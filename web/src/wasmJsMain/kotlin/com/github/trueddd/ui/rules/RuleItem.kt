package com.github.trueddd.ui.rules

import com.github.trueddd.utils.removeTabs

class RuleItem(
    val header: String,
    val body: Content,
)

sealed class Content {
    class Paragraph(val text: String) : Content()
    class OrderedList(val contentList: List<Paragraph>) : Content()
    class UnorderedList(val contentList: List<Paragraph>) : Content()
}

val MainInfo = RuleItem(
    header = "Общая информация",
    body = """
        |AGG2 - ивент, в котором стримеры соревнуются в том, кто первый доберётся до финиша на карте. 
        |Для того, чтобы сделать ход на карте, стримеру нужно пройти игру - 
        |Alawar-игру или специально подготовленную игру.
    """.removeTabs().let { Content.Paragraph(it) }
)

val HowToMakeMove = RuleItem(
    header = "Как делать ход",
    body = Content.OrderedList(listOf(
        Content.Paragraph("""
            |Стример бросает кубик d6. 
            |Выпавшее значение кубика - количество секторов, на которое стример перемещается.
        """.removeTabs()),
        Content.Paragraph("""
            |Стример роллит игру с сектора, на который он попал.
        """.removeTabs()),
        Content.Paragraph("""
            |После успешного прохождения игры стример завершает ход и повторяет предыдущие действия.
        """.removeTabs()),
        Content.Paragraph("""
            |Если игру пройти не получается, стример может дропнуть её. Стример кидает кубик d6, 
            |однако в этом случае перемещается назад.
        """.removeTabs()),
    ))
)

val WhatGamesToPlay = RuleItem(
    header = "В какие игры играть",
    body = Content.Paragraph("""
        |На карте есть 7 типов секторов - 6 жанровых и 1 специальный. С жанровыми секторами всё просто - выбирается игра, 
        |которая соответствует жанру на секторе, где находится стример. 
        |На специальном секторе сначала надо нароллить тип игры, 
        |а уже потом игру наролленого типа.
    """.removeTabs())
)

val WhatToReroll = RuleItem(
    header = "Какие игры можно рероллить",
    body = Content.UnorderedList(listOf(
        Content.Paragraph("Уже пройденная стримером игра в рамках AGG1 или AGG2"),
        Content.Paragraph("Игра, запуск которой технически сложен или невозможен"),
    ))
)

val HowToChooseDifficulty = RuleItem(
    header = "На какой сложности проходятся игры",
    body = Content.Paragraph("""
        |Обычно на той, которая выбрана по умолчанию, в ином случае стример может выбрать уровень сложности, 
        |кроме минимального. Однако если уровней сложности только два, можно выбрать минимальный.
    """.removeTabs())
)
