package com.github.trueddd.core

object Router {
    /** Called once user opens web app, returns game config **/
    const val CONFIG = "/config"
    /** Resource for wheel items' icons **/
    const val ICONS = "/icons"
    /** Resource for receiving game state and sending actions **/
    const val STATE = "/state"
    /** Resource for receiving users' actions **/
    const val ACTIONS = "/actions"
    /** Resource for receiving users' turns (profile tab) **/
    const val TURNS = "/turns"
    @Deprecated("May be deleted in the future")
    const val LOAD_GAME = "/game"
    /** Resource for user authorizing **/
    const val USER = "/user"
    /** Wheel related resources **/
    object Wheels {
        const val GAMES = "/games"
        const val ROLL_GAMES = "/games/roll"
        const val ITEMS = "/items"
        const val ROLL_ITEMS = "/items/roll"
        const val ROLL_PLAYERS = "/players/roll"
    }
}
