package cashkaro.com.listwhodoesnotcall;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.StaleDataException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnCheckBoxClick {

    private static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;
    private DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Contact> contactInfoList;

    private List<Contact> deleteContactList;
    private TextView msg;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deleteContactList = new ArrayList<>();
        databaseHandler = new DatabaseHandler(this);
        sharedPreferences = getSharedPreferences("Contact", MODE_PRIVATE);

//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();

        msg = (TextView) findViewById(R.id.msg);

        startAlert();

//        SharedPreferences.Editor editor1 = sharedPreferences.edit();
//        editor1.putBoolean("isFirstTime", true);
//        editor1.commit();


        contactInfoList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapter(this, contactInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_CALL_LOG,
                            Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.VIBRATE

                    },
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);


        }


        boolean isDatabaseClear = getIntent().getBooleanExtra("clearDatabase", false);

        Toast.makeText(this, "" + isDatabaseClear, Toast.LENGTH_SHORT).show();

        if (isDatabaseClear) {
//            Toast.makeText(getApplicationContext(), "Database Clear Called", Toast.LENGTH_SHORT).show();
            databaseHandler.deleteAllRecord();

            new AsyncTask<Object, Object, List<Contact>>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

//                    Utils.showProgress(MainActivity.this, "Loading...");
                }

                @Override
                protected List<Contact> doInBackground(Object[] objects) {
                    List<Contact> list = getCallDetails();
                    return list;
                }

                @Override
                protected void onPostExecute(List<Contact> NotCalledList) {

//                    Utils.hideProgress();

                    menuItem.setVisible(false);
                    if (NotCalledList.size() > 0) {
                        msg.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerViewAdapter.updateList(NotCalledList);
//                        menuItem.setVisible(true);
                    } else {
                        msg.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
//                        menuItem.setVisible(false);
                    }

                }
            }.execute();


        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);

//                    onResume();
                    if (sharedPreferences.getBoolean("isFirstTime", true)) {
                        new GetCallHistory().execute();
                    } else {
                        new AsyncTask<Object, Object, List<Contact>>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
//                                Utils.showProgress(MainActivity.this, "Loading...");
                            }

                            @Override
                            protected List<Contact> doInBackground(Object[] objects) {
                                List<Contact> list = getContacts();

                                Log.e(TAG, "doInBackground: Calling the database on create ");

                                return list;
                            }

                            @Override
                            protected void onPostExecute(List<Contact> NotCalledList) {

//                                Utils.hideProgress();

//                    menuItem.setVisible(false);
                                if (NotCalledList.size() > 0) {
                                    msg.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerViewAdapter.updateList(NotCalledList);
//                        menuItem.setVisible(true);
                                } else {
                                    msg.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
//                        menuItem.setVisible(false);
                                }

                            }
                        }.execute();
                        Log.e(TAG, "onCreate: called ");
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {

        Log.e(TAG, "onResume: " + sharedPreferences.getBoolean("isFirstTime", true));

//        if (sharedPreferences.getBoolean("isFirstTime", true)) {
//            new GetCallHistory().execute();
//        } else {

        new AsyncTask<Object, Object, List<Contact>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Utils.showProgress(MainActivity.this, "Loading...");
            }

            @Override
            protected List<Contact> doInBackground(Object[] objects) {
                List<Contact> list = getContacts();
                Log.e(TAG, "doInBackground: Calling the database  onresume ");
                return list;
            }

            @Override
            protected void onPostExecute(List<Contact> NotCalledList) {

                Utils.hideProgress();

//                    menuItem.setVisible(false);
                if (NotCalledList.size() > 0) {
                    msg.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerViewAdapter.updateList(NotCalledList);
//                        menuItem.setVisible(true);
                } else {
                    msg.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
//                        menuItem.setVisible(false);
                }

            }
        }.execute();

        Log.e(TAG, "onCreate: called kitkat ");
//        }


//        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu

        menuItem = menu.findItem(R.id.delete);
        menuItem.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:

                new AsyncTask<Object, Object, List<Contact>>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        Utils.showProgress(MainActivity.this, "Loading...");
                    }

                    @Override
                    protected List<Contact> doInBackground(Object[] objects) {
                        List<Contact> list = null;

                        for (Contact contact : deleteContactList
                                ) {

                            deleteContact(getApplicationContext(), contact.getPhoneNumber(), "");

                        }

                        deleteContactList.clear();


                        list = getContacts();

                        return list;
                    }

                    @Override
                    protected void onPostExecute(List<Contact> NotCalledList) {

                        Utils.hideProgress();

                        menuItem.setVisible(false);
                        if (NotCalledList.size() > 0) {
                            msg.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerViewAdapter.updateList(NotCalledList);
//                        menuItem.setVisible(true);
                        } else {
                            msg.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
//                        menuItem.setVisible(false);


                        }

                    }
                }.execute();


//                Toast.makeText(getApplicationContext(), "Item 1 Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void OnItemClickAdd(Contact contact) {

        deleteContactList.add(contact);

        showHideMenu();
    }

    private void showHideMenu() {

        if (deleteContactList.size() > 0) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
    }

    @Override
    public void OnItemClickRemove(Contact contact) {

        deleteContactList.remove(contact);
        showHideMenu();
    }

    private class GetCallHistory extends AsyncTask<Object, Object, List<Contact>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Utils.showProgress(MainActivity.this, "Loading...");
        }

        @Override
        protected List<Contact> doInBackground(Object[] objects) {

            Log.e(TAG, "doInBackground: called ");

            List<Contact> list = null;
            if (sharedPreferences.getBoolean("isFirstTime", true)) {

                list = getCallDetails();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstTime", false);
                editor.commit();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Contact> NotCalledList) {


            Utils.hideProgress();

            menuItem.setVisible(false);
            if (NotCalledList.size() > 0) {
                msg.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewAdapter.updateList(NotCalledList);
//                        menuItem.setVisible(true);
            } else {
                msg.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
//                        menuItem.setVisible(false);


            }

        }
    }


    private List<Contact> getCallDetails() {
        return getContacts();
    }

    public void onDeleteAllRecords(View view) {

        databaseHandler.deleteAllRecord();
    }


    public List<Contact> getContacts() {

        List<Contact> contactList = new ArrayList<>();
        final List<Contact> NotCalledList = new ArrayList<>();
        try {

            Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int nameid = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                String contactName = managedCursor.getString(nameid);
                databaseHandler.addContact(new Contact(0, contactName, phNumber));
                Log.e(TAG, "getCallDetails:contactName    " + contactName + "  number  " + phNumber);
            }
            managedCursor.close();


            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                Contact contact = new Contact();
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contact.setName(name);
                contact.setPhoneNumber(phoneNumber);
                contactList.add(contact);
            }
            phones.close();

            List<Contact> list = databaseHandler.getAllContacts();
            Set<Contact> setList = checkWhoDoesNotCall(list, contactList);
            NotCalledList.addAll(setList);
        } catch (RuntimeException e) {
            Log.e(TAG, "Cursor Error: " + e.getMessage());
        }
        return NotCalledList;

    }


    public Set<Contact> checkWhoDoesNotCall(List<Contact> listP, List<Contact> contactListP) {

        List<Contact> list = removeDuplicateFromList(listP);
        List<Contact> contactList = removeDuplicateFromList(contactListP);
        Set<Contact> newList = new HashSet<>();
        for (Contact contact : list
                ) {

            for (Contact contact1 : contactList
                    ) {
                if (contact.getPhoneNumber().replaceAll("\\s+", "").replaceAll("[-,()]", "").equals(contact1.getPhoneNumber().replaceAll("\\s+", "").replaceAll("[-,()]", ""))) {
                    newList.add(contact1);
                }
            }
        }
        Set<Contact> notcalledList = new HashSet<>();

        notcalledList.addAll(contactList);

        for (Contact contact : contactList
                ) {

            for (Contact contact1 : newList
                    ) {

//                Log.e(TAG, "checkWhoDoesNotCall: " + contact.getPhoneNumber() + "      " + contact1.getPhoneNumber());

                if (contact.getPhoneNumber().equals(contact1.getPhoneNumber())) {

                    notcalledList.remove(contact);

                    break;
                }

            }
        }


        return notcalledList;

    }


    public List<Contact> removeDuplicateFromList(List<Contact> list) {
        int s = 0;
        List<Contact> list1 = new ArrayList<Contact>();
        for (Contact us1 : list) {
            for (Contact us2 : list1) {
                if (us1.getPhoneNumber().equalsIgnoreCase(us2.getPhoneNumber())) {
                    s = 1;
                } else {
                    s = 0;
                }

            }
            if (s == 0) {
                list1.add(us1);
            }

        }
        return list1;
    }

    public static boolean deleteContact(Context ctx, String phone, String name) {
        Log.e(TAG, "deleteContact: " + phone);
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
//                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.NUMBER)).equalsIgnoreCase(name)) {
                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    ctx.getContentResolver().delete(uri, null, null);


                    return true;
//                    }

                } while (cur.moveToNext());
            }

        } catch (RuntimeException e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }


    //pass the context, so you are independant from any activity
    public void addReminder(Context context) {

        Intent alarmIntent = new Intent(context, WeeklyReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }

    public void startAlert() {

        Intent intent = new Intent(this, WeeklyReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 88, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                + (10 * 1000), pendingIntent);

//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
//                1000 * 20, pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + (1 * 60 * 1000), 1 * 60 * 1000, pendingIntent);

//        Toast.makeText(this, "Alarm set in " + 10 + " seconds", Toast.LENGTH_LONG).show();
    }

}
