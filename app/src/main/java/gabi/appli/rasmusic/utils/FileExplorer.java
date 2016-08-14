package gabi.appli.rasmusic.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import gabi.appli.rasmusic.R;
import gabi.appli.rasmusic.main.RasMusicMain;
import gabi.appli.rasmusic.main.RasMusicMainFragment;


/**
 * Created by gabi on 26/03/16.
 */
public class FileExplorer extends Activity {

    private List<String> item = null;
    private List<String> path = null;
    private String root;
    private TextView myPath;
    private String currPath;

    ListView explorer;

    /**
     * Called when the activity is first created.
     */

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer_layout);
        this.explorer = (ListView) findViewById(R.id.explorer_list);
        this.myPath = (TextView) findViewById(R.id.explorer_current_path);
        this.root = "/storage";
        this.currPath = "/storage";
        getDir(this.root);
        this.explorer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });

    }

    private void getDir(String dirPath)

    {
        File f = new File(dirPath);
        File[] files = f.listFiles();

        myPath.setText("Location: " + dirPath);
        this.currPath = dirPath;
        item = new ArrayList<String>();
        path = new ArrayList<String>();

        if (!dirPath.equals(root))
        {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++)
        {
            File file = files[i];
            if(file.canRead()) {
                path.add(file.getPath());
                if (file.isDirectory()) {
                    item.add(file.getName() + "/");
                } else {
                    item.add(file.getName());
                }
            }
        }

        ArrayAdapter<String> fileList =
                new ArrayAdapter<String>(this, R.layout.explorer_row_layout, R.id.explorer_row_item, item);
        this.explorer.setAdapter(fileList);

    }

    protected void onListItemClick(int position) {
        File file = new File(path.get(position));

        if (file.isDirectory())
        {
            if (file.canRead())
                getDir(path.get(position));
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {


                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                    }

                                }).show();

            }
        } else
        {
            Intent myIntent = new Intent(this, RasMusicMain.class);
            myIntent.putExtra(Common.SONG_PATH_EXTRA,file.getAbsolutePath());
            startActivity(myIntent);
        }
    }

    @Override
    public void onBackPressed() {
        // your code.
        if(this.currPath.equals(this.root)){
            super.onBackPressed();
        } else {
            this.getDir(this.currPath.substring(0, this.currPath.lastIndexOf("/")));
        }
    }
}