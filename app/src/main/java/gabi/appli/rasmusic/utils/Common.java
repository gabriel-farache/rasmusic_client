package gabi.appli.rasmusic.utils;

/**
 * Created by gabi on 26/03/16.
 */
public class Common {
    public static final int DEFAULT_PORT = 8888;
    public static final int DEFAULT_SONG_LEVEL = 75;
    public static final int MAX_SOUND_LEVEL = 100;

    public static final String GET_PLAYING_SONG = "/playingInfo";
    public static final String GET_PLAYLIST = "/playlist";
    public static final String GET_NEXT_SONG = "/nextSong";
    public static final String POST_ADD_AND_PLAY = "/play";
    public static final String PUT_ADD_TO_PLAYLIST = "/addToPlaylist";
    public static final String PATCH_PLAY_PAUSE = "/pauseResume";
    public static final String PATCH_NEXT = "/next";
    public static final String DELETE_STOP = "/stop";
    public static final String PATCH_SOUND_UP = "/soundUp";
    public static final String PATCH_SOUND_DOWN = "/soundDown";
    public static final String PATCH_SET_SOUND_LEVEL = "/setSoundLevel";
    public static final String PLAY_SONG_IN_PLAYLIST = "/playSongPlaylist";


    public static final String SONG_PATH_EXTRA = "songPath";
    public static final String SERVER_ADDR_EXTRA = "serverAddr";
    public static final String MAIN_FRAGMENT_TAG = "fragment_ras_music_main_layout";


}
