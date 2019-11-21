package tn.meteor.creavas.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tn.meteor.creavas.R;
import tn.meteor.creavas.adapters.UserTemplatesAdapter;
import tn.meteor.creavas.kitchen.ui.CreavasActivity;
import tn.meteor.creavas.models.Template;
import tn.meteor.creavas.models.Template_Table;


public class UserCreavasActivity extends AppCompatActivity {


    @BindView(R.id.add)
    ImageButton add;

    @BindView(R.id.starttext)
    TextView startText;
    @BindView(R.id.bottomBar_add)
    BottomBar bottomBarAdd;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_creavas);
        // getSupportActionBar().setTitle("Draft");
        ButterKnife.bind(this);
        mAuth=FirebaseAuth.getInstance();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("+ <- Clicked!");
                Intent intent = new Intent(getApplicationContext(), CreavasActivity.class);
                startActivity(intent);
                     }
        });
        List<Template> templatesList = SQLite.select().
                from(Template.class).where(Template_Table.idUser.is(mAuth.getUid())).queryList();

        tempGridSetup(templatesList);

       bottomBarAdd.setOnTabReselectListener(new OnTabReselectListener() {
           @Override
           public void onTabReSelected(int tabId) {
               if (tabId == R.id.add_creavas) {
                   Intent intent = new Intent(getApplicationContext(), CreavasActivity.class);
                   startActivity(intent);

               }
           }
       });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar_usercreavas, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            Intent intent = new Intent(getApplicationContext(), CreavasActivity.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.action_close) {
            this.finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }




    @BindView(R.id.creavas)
    GridView creavas;

    private void tempGridSetup(List<Template> templates) {


        if (templates.size() == 0) {
            add.setVisibility(View.VISIBLE);
            startText.setVisibility(View.VISIBLE);
            creavas.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.GONE);
            startText.setVisibility(View.GONE);

            setupImageGrid(templates);
        }
    }

    private void setupImageGrid(List<Template> templates) {


        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / 2;
        creavas.setColumnWidth(imageWidth);

        UserTemplatesAdapter adapter = new UserTemplatesAdapter(this, R.layout.layout_grid_imageview, "", templates);

        creavas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UserCreavasActivity.this, CreavasActivity.class);

                Template template = (Template) creavas.getAdapter().getItem(i);
                Toast.makeText(UserCreavasActivity.this, template.getId() + "", Toast.LENGTH_SHORT).show();
                intent.putExtra("template", template);
                startActivity(intent);
                finish();
            }
        });
        creavas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showChoicesDialog(templates, i);
                return true;
            }
        });
        creavas.setAdapter(adapter);
    }

    private ProgressDialog progressDialog;

    public void showChoicesDialog(List<Template> templates, int i) {
        new MaterialDialog.Builder(this)
                .items(R.array.userCreavas)
                .itemsCallback((dialog, view1, which, text) -> {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    if (which == 0) {
                        Intent intent = new Intent(UserCreavasActivity.this, CreavasActivity.class);
                        Template template = (Template) creavas.getAdapter().getItem(i);
                        Toast.makeText(UserCreavasActivity.this, template.getId() + "", Toast.LENGTH_SHORT).show();
                        intent.putExtra("template", template);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    } else if (which == 1) {
                        Template template = (Template) creavas.getAdapter().getItem(i);
                        template.delete();
                        templates.remove(templates.indexOf(template));
                        tempGridSetup(templates);
                        progressDialog.dismiss();
                    }

                })
                .show();
    }

    private Toast toast;

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
