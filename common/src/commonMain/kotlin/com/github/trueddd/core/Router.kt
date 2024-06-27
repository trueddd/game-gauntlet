package com.github.trueddd.core

object Router {
    /** Called once user opens web app, returns game config **/
    const val CONFIG = "/config"
    /** Resource for wheel items' icons **/
    const val ICONS = "/icons"
    /** Resource for receiving game state and sending actions **/
    const val STATE = "/state"
    /** Resource for receiving game state (unauthorized) **/
    const val SNAPSHOT = "/state/snapshot"
    /** Resource for receiving users' actions **/
    const val ACTIONS = "/actions"
    /** Resource for receiving users' turns (profile tab) **/
    const val TURNS = "/turns"
    /** Resource for user authorizing **/
    const val USER = "/user"
    /** Resource for loading remote data (proxy) **/
    const val REMOTE = "/remote"
    /** Resource for user's rewards on Twitch (global events, e.g. Nuke and Tornado) **/
    const val REWARD = "/reward"
    /** Wheel related resources **/
    object Wheels {
        const val GAMES = "/games"
        const val ROLL_GAMES = "/games/roll"
        const val ROLL_ITEMS = "/items/roll"
        const val ROLL_PLAYERS = "/players/roll"
    }
}
