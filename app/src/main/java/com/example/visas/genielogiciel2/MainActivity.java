package com.example.visas.genielogiciel2;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;
import com.venus.app.IO.Asyncable;
import com.venus.app.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Asyncable {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Contact_DAO contact_dao;
    private static final String FOA_EXPORT = "export";
    private static final String FOA_IMPORT = "import";
    private static Groupe_DAO groupe_dao;
    private static String url, numéro;
    private static Context tempContext;


    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contact_dao = new Contact_DAO(getApplicationContext());
        groupe_dao = new Groupe_DAO(getApplicationContext());
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentMessages(), "Messages");
        adapter.addFragment(new FragmentGroups(), "Groupes");
        adapter.addFragment(new FragmentContacts(), "Contacts");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_message);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_group);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_contacts_black_24dp);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);

        tempContext = this;

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        /*Toast.makeText(getApplicationContext(),manager.getNetworkOperatorName(),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),manager.getLine1Number(),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),manager.getLine1Number(),Toast.LENGTH_LONG).show();

        SubscriptionManager subs= (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        Toast.makeText(getApplicationContext(),String.valueOf(subs.getActiveSubscriptionInfoCount()),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),String.valueOf(SmsManager.getDefaultSmsSubscriptionId()),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),String.valueOf(subs.getActiveSubscriptionInfoList().get(1).getDisplayName()),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),String.valueOf(manager.getPhoneType()),Toast.LENGTH_LONG).show();*/



    }
    private static String getOutput(Context context, String methodName, int slotId) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        String reflectionMethod = null;
        String output = null;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            for (Method method : telephonyClass.getMethods()) {
                String name = method.getName();
                if (name.contains(methodName)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1 && params[0].getName().equals("int")) {
                        reflectionMethod = name;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephony, reflectionMethod, slotId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }
    private static String getOpByReflection(TelephonyManager telephony, String predictedMethodName, int slotID, boolean isPrivate) {

        //Log.i("Reflection", "Method: " + predictedMethodName+" "+slotID);
        String result = null;

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID;
            if (slotID != -1) {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName, parameter);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
                }
            } else {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName);
                }
            }

            Object ob_phone;
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            if (getSimID != null) {
                if (slotID != -1) {
                    ob_phone = getSimID.invoke(telephony, obParameter);
                } else {
                    ob_phone = getSimID.invoke(telephony);
                }

                if (ob_phone != null) {
                    result = ob_phone.toString();

                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        //Log.i("Reflection", "Result: " + result);
        return result;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_restaurer) {
            showConnexionDialog(false);

            return true;
        }
        if(id==R.id.action_sauvegarder){
            showConnexionDialog(true);

            return true;
        }
        if(id==R.id.action_parametres){
            Intent intent=new Intent(MainActivity.this,parameterActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConnexionDialog(final boolean sauvegarde) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connexion_dialog);

        final EditText numero=dialog.findViewById(R.id.conexion_tel);

        Button cancelButton = dialog.findViewById(R.id.connexion_cancel_btn);
        Button saveButton = dialog.findViewById(R.id.connexion_save_btn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to add contacts
                if(!numero.getText().toString().matches("")) {
                        numéro=numero.getText().toString();
                        url="?telephone="+numéro;

                        if( !sauvegarde){
                    ImportExportDialogFragment.newInstance("import").show(getSupportFragmentManager(), "import");
                        }
                        else{
                            ImportExportDialogFragment.newInstance("export").show(getSupportFragmentManager(), "export");
                        }
                }
                    dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void fetchOnlineResult(Object result, String code) {
        if (code.equals(FOA_EXPORT)) {
            // resultat de la sauvegarde
                if(result==null){
                    Toast.makeText(getApplicationContext(),"Sauvegarde effectuée avec succès",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Echec de la sauvegarde",Toast.LENGTH_LONG).show();
                }

        } else if (code.equals(FOA_IMPORT)) {
            // resultat de la restauration
            if(result==null) {

                Toast.makeText(getApplicationContext(),"Aucun élement sauvegardé sur le serveur",Toast.LENGTH_LONG).show();
            }
            else{
                JSONArray jsonObjects= null;
                String anciennom="";

                try {
                    jsonObjects = new JSONArray(result.toString());
                    ArrayList<Contact> contacts=new ArrayList<>();
                    JSONObject jsoObject= (JSONObject) jsonObjects.get(0);
                    anciennom=jsoObject.getString("nomgroupe");
                    String contact=jsoObject.getString("nomcontact");
                    int numero=Integer.parseInt(jsoObject.getString("numero"));
                    contact_dao.enregisterContact(new Contact(contact,numero));
                    contacts.add(new Contact(contact,numero));

                    for(int i=1;i<jsonObjects.length();i++){
                        JSONObject jsonObject= (JSONObject) jsonObjects.get(i);
                        String nomGroupe=jsonObject.getString("nomgroupe");
                        if(anciennom==nomGroupe){
                            String nomContact=jsonObject.getString("nomcontact");
                            int num=Integer.parseInt(jsonObject.getString("numero"));
                            contact_dao.enregisterContact(new Contact(nomContact,num));
                            contacts.add(new Contact(nomContact,num));
                        }else{
                            groupe_dao.enregisterGroupe(new Groupe(Integer.parseInt(jsonObject.getString("idgroupe")),anciennom,contacts));
                            System.out.println(contacts);
                            contacts.clear();
                            anciennom=nomGroupe;
                            String nomContact=jsonObject.getString("nomcontact");
                            int num=Integer.parseInt(jsonObject.getString("numero"));
                            contact_dao.enregisterContact(new Contact(nomContact,num));
                            contacts.add(new Contact(nomContact,num));
                    }
                        if(!contacts.isEmpty())
                            groupe_dao.entregistrerContactsGroupe(contacts,groupe_dao.selectionnerIdGroupe(nomGroupe));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FragmentGroups.onresu=true;
            }
        }
    }

    public static class ImportExportDialogFragment extends AppCompatDialogFragment {
        //TODO: Ajouter les demandes de permission
        private static final String ARG_OP = "operation";
        private AppCompatEditText editText;
        private ArrayList<Contact> contactList;
        private ArrayList<Groupe> groupeList= MainActivity.groupe_dao.selectionnerGroupes();

        public static ImportExportDialogFragment newInstance(String op) {

            Bundle args = new Bundle();
            args.putString(ARG_OP, op);
            ImportExportDialogFragment fragment = new ImportExportDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        private void exporter(String url) {
            JSONObject[] tab = new JSONObject[200];
            try {

                int id;
                String name;
                int i = 0;
                for (Groupe g:groupeList) {
                    contactList = g.getContacts();
                    name = g.getGroupName();
                    id = g.getId();
                    for (Contact c: contactList){
                        JSONObject tomJsonObj = new JSONObject();
                        tomJsonObj.put("idgroupe", id+"");
                        tomJsonObj.put("nomcontact", c.getContactName());
                        tomJsonObj.put("nomgroupe", name);
                        tomJsonObj.put("numero", c.getContactNumber()+"");
                        new SendToServerAsc((MainActivity) getActivity(), url, FOA_EXPORT).execute(tomJsonObj.toString());
                        tab[i] = tomJsonObj;
                        i++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismiss();
        }

        private void importer(String url) {
            new FetchOnlineAsc((MainActivity) getActivity(), url, FOA_IMPORT).execute();
            dismiss();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View v = new LinearLayout(getContext());
            ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);
            editText = new AppCompatEditText(getContext());
            ((LinearLayout) v).addView(editText);

            AlertDialog dialog = null;
            if (getArguments().containsKey(ARG_OP))
                if (getArguments().getString(ARG_OP).equals("export")) {
                    dialog =  new AlertDialog.Builder(getContext())
                            .setView(v)
                            .setTitle("Entrez l'adresse du serveur")
                            .setPositiveButton("Exporter", null)
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Utils.isGoodStringValue(editText.getText().toString())){
                                        exporter(editText.getText().toString());
                                        System.out.println(editText.getText().toString());
                                    }
                                    else editText.setError("valeur incorrecte");
                                }
                            });
                        }
                    });
                } else if (getArguments().getString(ARG_OP).equals("import")) {
                    dialog =  new AlertDialog.Builder(getContext())
                            .setView(v)
                            .setTitle("Entrez l'adresse du serveur")
                            .setPositiveButton("Importer", null)
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Utils.isGoodStringValue(editText.getText().toString()+MainActivity.url)){
                                        importer(editText.getText().toString()+MainActivity.url);
                                        System.out.println(editText.getText().toString()+MainActivity.url);
                                    }
                                    else editText.setError("valeur incorrecte");
                                }
                            });
                        }
                    });
                }

            if (dialog == null) dialog = new AlertDialog.Builder(getContext()).create();
            return dialog;
        }
    }

    public static class FetchOnlineAsc extends AsyncTask<String, Integer, JSONArray> {

        private com.venus.app.IO.Asyncable mSource = null;
        private String mUrl = null;
        ProgressDialog dialog;
        private String code;

        public FetchOnlineAsc(com.venus.app.IO.Asyncable source, String url, String code){
            mSource = source;
            mUrl = url;
            this.code = code;
        }

        public com.venus.app.IO.Asyncable getSource() {
            return mSource;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = Utils.newLoadingDialog(MainActivity.tempContext);
            dialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            JSONArray jsonArray = null;
            String reponse = null;
            String data="";
            if(!isCancelled()){
                try {
                    URL url = new URL(mUrl);
                    URLConnection conn =  url.openConnection();
                    conn.setConnectTimeout(5000);// temps de recherche de la connexion
                    data=URLEncoder.encode("telephone","UTF-8")+"="+URLEncoder.encode(MainActivity.numéro);
                    conn.setReadTimeout(10000); // temps de lecture des donnees
                    conn.setDoOutput(true);

                    // on envoie les donnees
                    if (strings.length != 0){

                        com.venus.app.IO.JSONfunctions.sendData(conn, data);
                    }
                    // read the response
                    reponse = com.venus.app.IO.JSONfunctions.getData(conn);
                    jsonArray = new JSONArray(reponse);
                } catch (Exception e) {
                    Log.e("log_cat", e.getClass().getSimpleName() + ": " + e.getMessage());
                }
                System.out.println("entree = " + mUrl + ", " + (strings.length != 0 ? data : ""));
                System.out.println("reponse = " + reponse);
            }
            return jsonArray;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onCancelled(){
            onPostExecute(null);
        }

        @Override
        protected void onPostExecute(JSONArray result){
            dialog.dismiss();
            mSource.fetchOnlineResult(result, code);
        }
    }

    public static class SendToServerAsc extends AsyncTask<String, Void, String> {

        private com.venus.app.IO.Asyncable mSource;
        private String murl = null;
        private String code;
        private String numero;
        ProgressDialog dialog;

        public SendToServerAsc(com.venus.app.IO.Asyncable asyncable, String url, String code){
            mSource = asyncable;
            murl = url;
            this.code = code;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = Utils.newLoadingDialog(MainActivity.tempContext);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String reponse = null;
            String data="";
            if (!isCancelled()) {

                try {
                    URL url = new URL(murl);
                    URLConnection connection =url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(10000);
                    data=URLEncoder.encode("telephone","UTF-8")+"="+URLEncoder.encode(MainActivity.numéro);
                    data+="&"+URLEncoder.encode("groupes","UTF-8")+"="+URLEncoder.encode("");
                    connection.setDoOutput(true);
                    // on ecrit les donnees
                    com.venus.app.IO.JSONfunctions.sendData(connection, data+strings[0]);

                    // et on lit la reponse du serveur
                    reponse = com.venus.app.IO.JSONfunctions.getData(connection);
                } catch (Exception e) {
                    Log.e("log_cat", e.getClass().getSimpleName() + ": " + e.getMessage());
                    cancel(true);
                }
            }
            System.out.println("data = " +data+strings[0]);
            System.out.println("reponse = " + reponse);
            return reponse;
        }

        @Override
        protected void onCancelled(){
            // on n'a pas pu se connecter au serveur
            onPostExecute(null);
        }

        @Override
        protected void onPostExecute(String result){
            dialog.dismiss();
            mSource.fetchOnlineResult(result, code);
        }
    }



}
