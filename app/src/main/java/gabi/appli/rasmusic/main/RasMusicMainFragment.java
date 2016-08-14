package gabi.appli.rasmusic.main;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gabi.appli.rasmusic.R;
import gabi.appli.rasmusic.utils.Common;
import gabi.appli.rasmusic.utils.FileExplorer;
import gabi.appli.rasmusic.utils.HTTPUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class RasMusicMainFragment extends Fragment {

    public static RasMusicMainFragment me;

    private static String serverAddr = null;
    public static List<String> playlist;

    private Button validateServerAddrBtn;
    private Button addAndPlayBtn;
    private Button addToPlaylistBtn;

    private ImageButton playPauseBtn;
    private ImageButton nextBtn;
    private ImageButton stopBtn;
    private ImageButton soundUp;
    private ImageButton soundDown;
    private ImageButton searchBtn;

    private TextView serverAddrValue;
    private TextView songPathValue;

    private SeekBar soundLevelBar;

    private ListView playlistListView;

    public static ArrayAdapter<String> listViewAdapter;
    private Integer soundLevel;
    private boolean isPlaying;

    public RasMusicMainFragment() {
        this.me = this;
        this.playlist = new ArrayList<String>();
        this.isPlaying = false;
        this.soundLevel = Common.DEFAULT_SONG_LEVEL;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ras_music_main, container, false);

        this.initViewComponents(view);
        this.initComponentsListener();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String songPath = getActivity().getIntent().getStringExtra(Common.SONG_PATH_EXTRA);
        if (songPath != null) {
            this.songPathValue.setText(songPath);
            this.searchBtn.setImageResource(R.drawable.close_black);
        }

        if (RasMusicMainFragment.serverAddr != null) {
            this.serverAddrValue.setText(RasMusicMainFragment.serverAddr);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    private void initViewComponents(View view) {
        this.validateServerAddrBtn = (Button) view.findViewById(R.id.serverAddrValBtn);
        this.addAndPlayBtn = (Button) view.findViewById(R.id.addAndPlayBtn);
        this.addToPlaylistBtn = (Button) view.findViewById(R.id.addToPlaylistBtn);

        this.playPauseBtn = (ImageButton) view.findViewById(R.id.playPauseBtn);
        this.nextBtn = (ImageButton) view.findViewById(R.id.nextBtn);
        this.stopBtn = (ImageButton) view.findViewById(R.id.stopBtn);
        this.soundDown = (ImageButton) view.findViewById(R.id.soundDownBtn);
        this.soundUp = (ImageButton) view.findViewById(R.id.soundUpBtn);
        this.searchBtn = (ImageButton) view.findViewById(R.id.searchBtn);

        this.serverAddrValue = (TextView) view.findViewById(R.id.serverAddrValue);
        this.songPathValue = (TextView) view.findViewById(R.id.songPathValue);

        this.soundLevelBar = (SeekBar) view.findViewById(R.id.soundLevel);
        this.soundLevelBar.setProgress(this.soundLevel);
        this.soundLevelBar.setMax(Common.MAX_SOUND_LEVEL);

        this.playlistListView = (ListView) view.findViewById(R.id.playlist);
        this.listViewAdapter = new ArrayAdapter(getActivity(), R.layout.playlist_layout, R.id.playlistRow, this.playlist);
        this.playlistListView.setAdapter(this.listViewAdapter);
    }

    private void initComponentsListener() {
        this.validateServerAddrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regex = "\\b((https?|ftp|file)://)?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                RasMusicMainFragment.serverAddr = serverAddrValue.getText().toString()
                        .replaceAll("^\\s+", "")
                        .replaceAll("\\s+$", "");
                if (RasMusicMainFragment.serverAddr.matches(regex)) {

                    if (RasMusicMainFragment.serverAddr.endsWith("/")) {
                        RasMusicMainFragment.serverAddr =
                                RasMusicMainFragment.serverAddr.substring(0,
                                        RasMusicMainFragment.serverAddr.length() - 2);
                    }

                    if (!RasMusicMainFragment.serverAddr.startsWith("http://")) {
                        RasMusicMainFragment.serverAddr = "http://" + RasMusicMainFragment.serverAddr;
                        serverAddrValue.setText(RasMusicMainFragment.serverAddr);
                    }

                    if (!RasMusicMainFragment.serverAddr.matches(".*:[0-9]{4}$")) {
                        RasMusicMainFragment.serverAddr += ":" + Common.DEFAULT_PORT;
                        serverAddrValue.setText(RasMusicMainFragment.serverAddr);
                    }
                    getPlaylist();

                } else {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Wrong Server Address")
                            .setMessage("The server address is wrong: " + RasMusicMainFragment.serverAddr +
                                    ".\nPlease give a valid server address.")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {


                                        @Override

                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                        }

                                    })
                            .show();
                }

            }
        });

        this.addAndPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateServerAddr();
                    String path = songPathValue.getText().toString();
                    String filename = path.substring(path.lastIndexOf("/") + 1);

                    addToPlaylistAndPlay(filename, path);
                    playPauseBtn.setImageResource(R.drawable.play_black);
                    playlist.add(0, filename);
                    isPlaying = true;
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.addToPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateServerAddr();
                    String path = songPathValue.getText().toString();
                    String filename = path.substring(path.lastIndexOf("/") + 1);

                    addToPlaylist(filename, path);
                    playlist.add(filename);
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        this.playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateServerAddr();
                    playPause();
                    isPlaying = !isPlaying;
                    if (isPlaying) {
                        playPauseBtn.setImageResource(R.drawable.play_black);
                    } else {
                        playPauseBtn.setImageResource(R.drawable.pause_black);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        this.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateServerAddr();
                    String songPath = songPathValue.getText().toString();
                    if (songPath != null && !songPath.isEmpty()) {
                        songPathValue.setText("");
                        searchBtn.setImageResource(R.drawable.search_black);
                    } else {
                        displayExplorer();
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.soundUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateServerAddr();
                    soundLevel++;
                    setSongLevel();
                    soundLevelBar.setProgress(soundLevel);
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.soundDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    validateServerAddr();
                    soundLevel--;
                    setSongLevel();
                    soundLevelBar.setProgress(soundLevel);
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.soundLevelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    validateServerAddr();
                    soundLevel = progress;
                    setSongLevel();
                    soundLevelBar.setProgress(soundLevel);

                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.nextBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                try {
                    validateServerAddr();
                    playNextSong();
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateServerAddr();
                    stopPlayer();
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.songPathValue.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                try {
                    validateServerAddr();
                    displayExplorer();

                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });

        this.playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    playSongInPlaylist(position);
                } catch (Exception e) {
                    System.err.println(e.getMessage() + "\n" + e.getStackTrace());
                    Toast.makeText(RasMusicMainFragment.me.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void playSongInPlaylist(Integer position) {
        String[] params = {
                RasMusicMainFragment.serverAddr,
                HTTPUtil.HTTPActions.PLAY_SONG_IN_PLAYLIST.toString(),
                position.toString()
        };
        new HTTPUtil().execute(params);
    }

    private void validateServerAddr() throws Exception {
        if (RasMusicMainFragment.serverAddr == null) {
            new AlertDialog.Builder(me.getActivity())
                    .setTitle("Missing Server Address")
                    .setMessage("The server address is missing.\nPlease give a server address.")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {


                                @Override

                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }

                            })
                    .show();
            ;
            throw new Exception("Empty server address");
        } else if (!RasMusicMainFragment.serverAddr.matches("^\\b((https?|ftp|file)://)?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]:[0-9]{4}$")) {
            new AlertDialog.Builder(me.getActivity())
                    .setTitle("Wrong Server Address")
                    .setMessage("The server address is wrong: " + RasMusicMainFragment.serverAddr +
                            ".\nPlease give a valid server address.")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {


                                @Override

                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }

                            })
                    .show();
            ;
            throw new Exception("Wrong server address");
        }
    }


    private void addToPlaylistAndPlay(String filename, String filePath) {
        String[] params = {
                RasMusicMainFragment.serverAddr,
                HTTPUtil.HTTPActions.ADDANDPLAY.toString(),
                filePath
        };
        new HTTPUtil().execute(params);
    }

    private void displayExplorer() {
        Intent myIntent;
        myIntent = new Intent(me.getActivity(), FileExplorer.class);
        startActivity(myIntent);
    }

    private void setSongLevel() {
        String[] params = {
                RasMusicMainFragment.serverAddr,
                HTTPUtil.HTTPActions.SET_SOUND_LEVEL.toString(),
                this.soundLevel.toString()
        };
        new HTTPUtil().execute(params);
    }

    private void addToPlaylist(String filename, String filePath) {
        String[] params = {
                RasMusicMainFragment.serverAddr,
                HTTPUtil.HTTPActions.ADDTOPLAYLIST.toString(),
                filePath,
                filename
        };
        new HTTPUtil().execute(params);
    }

    private void playPause() {
        String[] params = {
                RasMusicMainFragment.serverAddr,
                HTTPUtil.HTTPActions.PLAYSEPAUSE.toString()
        };
        new HTTPUtil().execute(params);
    }

    private void playNextSong() {
        String[] params = {
                RasMusicMainFragment.serverAddr,
                HTTPUtil.HTTPActions.NEXT.toString()
        };
        new HTTPUtil().execute(params);
    }

    private void stopPlayer() {
        String[] params = {
                RasMusicMainFragment.serverAddr,
                HTTPUtil.HTTPActions.STOP.toString()
        };
        new HTTPUtil().execute(params);
    }

    private void getPlaylist() {
        String[] params = {
                RasMusicMainFragment.serverAddr,
                HTTPUtil.HTTPActions.GET_PLAYLIST.toString()
        };
        new HTTPUtil().execute(params);
    }
}
