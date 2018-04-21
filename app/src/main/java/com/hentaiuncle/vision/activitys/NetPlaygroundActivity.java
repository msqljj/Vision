package com.hentaiuncle.vision.activitys;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hentaiuncle.vision.Global;
import com.hentaiuncle.vision.R;
import com.hentaiuncle.vision.other.NoScrollViewPager;
import com.hentaiuncle.vision.other.VisionList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipInputStream;

public class NetPlaygroundActivity extends AppCompatActivity {


    private NoScrollViewPager mViewPager;
    private TabLayout tabLayout;

    private String[] titles;
    private int[] views;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_playground);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titles = new String[]{"本地", "网络"};
        views = new int[]{R.layout.fragment_net_playground_local, R.layout.fragment_net_playground_network};

        mViewPager = (NoScrollViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tab);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setNoScroll(true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_net_playground, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class LocalFragment extends Fragment {

        private ListView list;
        int id;
        String[] ls = new String[0];
        File root = new File(Global.root + "/net/");
        ArrayAdapter adapter;

        public LocalFragment(int id) {
            this.id = id;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(id, container, false);
            list = rootView.findViewById(R.id.local_list);
            root.mkdirs();
            ls = root.list();
            adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, ls);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String m = Global.root + "/net/" + ls[i] + "/vision.txt";
                    Intent intent = new Intent(LocalFragment.this.getContext(),CnnTest.class);
                    intent.putExtra("path",m);
                    startActivity(intent);
                }
            });
            refresh();
            return rootView;
        }

        public void refresh(){
            ls = root.list();
            adapter.notifyDataSetChanged();
        }
    }

    public static class NetworkFragment extends Fragment {

        public static final String listurl = "http://www.hentaiuncle.com/vision/vision.list";
        int id;
        private ListView list;
        VisionList vl;
        List<String> ls;
        Handler handler = new Handler();
        LocalFragment lf;

        public NetworkFragment(int id,LocalFragment lf) {
            this.id = id;
            this.lf = lf;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(id, container, false);
            list = rootView.findViewById(R.id.net_list);

            try {
                String json = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            HttpURLConnection conn = (HttpURLConnection) new URL(listurl).openConnection();
                            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            int b;
                            byte[] buff = new byte[256];
                            while ((b = in.read(buff)) != -1)
                                out.write(buff, 0, b);
                            out.flush();
                            String json = new String(out.toByteArray());
                            return json;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(NetworkFragment.this.getContext(), "无法获取网络列表", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }
                }.execute().get();
                Gson gson = new GsonBuilder().create();
                vl = gson.fromJson(json, VisionList.class);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(NetworkFragment.this.getContext(), "无法获取网络列表", Toast.LENGTH_SHORT).show();
            }
            if (vl != null) {
                ls = new ArrayList<>();
                for (VisionList.item i : vl.list)
                    ls.add(i.name + "  -  " + i.intor);
                list.setAdapter(new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, ls));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        VisionList.item item = vl.list.get(i);
                        AlertDialog a = new AlertDialog.Builder(NetworkFragment.this.getContext())
                                .setTitle(item.name)
                                .setMessage("简介:  " + item.intor + "\n来源:" + item.url + "\n\n是否下载？")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.url));
                                        File f = new File(Global.root + "/download/");
                                        f.mkdirs();
                                        request.setDestinationUri(Uri.parse("file://" + Global.root + "/download/" + item.name + ".zip"));


                                        DownloadManager downloadManager = (DownloadManager) NetworkFragment.this.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                        long id = downloadManager.enqueue(request);
                                        ProgressDialog d = new ProgressDialog(NetworkFragment.this.getContext());
                                        d.setTitle("请稍等");
                                        d.setMessage("正在下载: " + item.name + "\nfrom:" + item.url);
                                        d.setCancelable(false);
                                        d.show();

                                        BroadcastReceiver receiver = new BroadcastReceiver() {
                                            @Override
                                            public void onReceive(Context context, Intent intent) {
                                                DownloadManager.Query query = new DownloadManager.Query();
                                                query.setFilterById(id);//筛选下载任务，传入任务ID，可变参数
                                                Cursor c = downloadManager.query(query);

                                                if(c.moveToFirst()){
                                                    File f = new File(Global.root + "/download/" + item.name + ".zip");
                                                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                                    if(status == DownloadManager.STATUS_FAILED) {
                                                        Toast.makeText(NetworkFragment.this.getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                                                        if(f.exists())
                                                            f.delete();
                                                        d.dismiss();
                                                    }if(status == DownloadManager.STATUS_SUCCESSFUL){
                                                        new Thread(){
                                                            @Override
                                                            public void run() {
                                                                handler.post(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        d.setMessage("正在解压文件…");
                                                                    }
                                                                });
                                                                Global.Unzip(Global.root + "/download/" + item.name + ".zip",Global.root + "/net/" + item.name + "/");
                                                                if(f.exists())
                                                                    f.delete();
                                                                handler.post(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        d.dismiss();
                                                                        startActivity(new Intent(NetworkFragment.this.getContext(),NetPlaygroundActivity.class));
                                                                        NetworkFragment.this.getActivity().finish();
                                                                    }
                                                                });
                                                            }
                                                        }.start();

                                                    }
                                                }
                                            }
                                        };
                                        NetworkFragment.this.getContext().registerReceiver(receiver,
                                                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                                    }
                                }).create();
                        a.setCancelable(false);
                        a.show();
                    }
                });
            }
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new LocalFragment(views[position]);
            return new NetworkFragment(views[position],(LocalFragment)getItem(0));
        }

        @Override
        public int getCount() {
            return views.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
