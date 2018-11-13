package specificstep.com.perfectrecharge_dist.ui.addUser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import butterknife.ButterKnife;
import specificstep.com.perfectrecharge_dist.Database.DatabaseHelper;
import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.GlobalClasses.MCrypt;
import specificstep.com.perfectrecharge_dist.GlobalClasses.TransparentProgressDialog;
import specificstep.com.perfectrecharge_dist.GlobalClasses.URL;
import specificstep.com.perfectrecharge_dist.Models.AddUserTypeModel;
import specificstep.com.perfectrecharge_dist.Models.Default;
import specificstep.com.perfectrecharge_dist.Models.User;
import specificstep.com.perfectrecharge_dist.Models.UserSchemeListModel;
import specificstep.com.perfectrecharge_dist.R;
import specificstep.com.perfectrecharge_dist.utility.InternetUtil;
import specificstep.com.perfectrecharge_dist.utility.Utility;

public class AddUserFragment extends Fragment implements View.OnClickListener {

    String TAG = "AddUserFragment :: ";

    String[] DayOfWeek = {"Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};

    Spinner mySpinner, spSchemeName;
    Button mBtncancel, mBtnSubmit;
    EditText mEtFirstName, mEtLastName, mEtMobileNo;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private final int SUCCESS = 1, ERROR = 2;
    private TransparentProgressDialog transparentProgressDialog;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userArrayList;
    private ArrayList<AddUserTypeModel> addUserTypeModelsList;
    AddUserTypeModel mAddUserTypeModel;
    private  ArrayList<UserSchemeListModel> userSchemeListModelArrayList;
    UserSchemeListModel mUserSchemeListModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        mySpinner = (Spinner) view.findViewById(R.id.spSchemeType);


        spSchemeName = (Spinner) view.findViewById(R.id.spSchemeName);
        mBtncancel = (Button) view.findViewById(R.id.mBtncancel);
        mBtnSubmit = (Button) view.findViewById(R.id.mBtnSubmit);

        mEtFirstName = (EditText) view.findViewById(R.id.mEtFirstName);
        mEtLastName = (EditText) view.findViewById(R.id.mEtLastName);
        mEtMobileNo = (EditText) view.findViewById(R.id.mEtMobileNo);

        mBtnSubmit.setOnClickListener(this);
        mBtncancel.setOnClickListener(this);

        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);
        databaseHelper = new DatabaseHelper(getActivity());
        /* [START] - get user data from database and store into string variables */
        userArrayList = databaseHelper.getUserDetail();
        // Store user information in variables
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        strRegistrationDateTime = userArrayList.get(0).getReg_date();
        // [END]

        //makeAddUserApiCalls();

        makeAddUserTypeApiCalls();

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeAddUserSchemeApiCalls();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private void makeAddUserTypeApiCalls() {

        //user type thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.add_user_type;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    System.out.println("Add user type response1: " + response);
                    myHandler.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtncancel:
                mEtFirstName.setText("");
                mEtLastName.setText("");
                mEtMobileNo.setText("");
                break;
            case R.id.mBtnSubmit:
                if(varifyFields()) {
                    makeAddUserApiCalls();
                }
                break;

        }
    }

    public boolean varifyFields() {

        if(TextUtils.isEmpty(mEtFirstName.getText())) {
            Toast.makeText(getActivity(),"Please Enter First Name",Toast.LENGTH_LONG).show();
            return false;
        } else if(TextUtils.isEmpty(mEtLastName.getText())) {
            Toast.makeText(getActivity(),"Please Enter Last Name",Toast.LENGTH_LONG).show();
            return false;
        } else if(TextUtils.isEmpty(mEtMobileNo.getText())) {
            Toast.makeText(getActivity(),"Please Enter Mobile No",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }

    }

    //Method: submit add user
    public void makeAddUserApiCalls() {

        //add user thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.add_user_register;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app",
                            "scheme_name",
                            "user_type_id",
                            "first_name",
                            "last_name",
                            "phone_no"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION,
                            userSchemeListModelArrayList.get(spSchemeName.getSelectedItemPosition()).getScheme_name(),
                            addUserTypeModelsList.get(mySpinner.getSelectedItemPosition()).getAddUserId(),
                            mEtFirstName.getText().toString(),
                            mEtLastName.getText().toString(),
                            mEtMobileNo.getText().toString()
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    System.out.println("Add user scheme response1: " + response);
                    myHandlerSubmit.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandlerSubmit.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    // handle user scheme thread messages
    private Handler myHandlerSubmit = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseSubmitSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    //parse scheme api
    public void parseSubmitSuccessResponse(String response) {

        Log.e(TAG, " AccountLedger Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();

                mEtFirstName.setText("");
                mEtLastName.setText("");
                mEtMobileNo.setText("");
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
                //Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
            } else if (jsonObject.getString("status").equals("2")) {
                    Toast.makeText(getActivity(),""+jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
            } else {

            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }

    }

    // dismiss progress dialog
    private void dismissProgressDialog() {
        try {
            if (transparentProgressDialog != null) {
                if (transparentProgressDialog.isShowing())
                    transparentProgressDialog.dismiss();
            }
        }
        catch (Exception ex) {
            Log.e(TAG,"Error in dismiss progress");
            Log.e(TAG,"Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // parse success response
    private void parseSuccessResponse(String response) {
        Log.e(TAG, " AccountLedger Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = decryptAPI(encrypted_response);
                System.out.println("Add User Type: decrypted_response : " + decrypted_response);
                //Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
                parsing_response(decrypted_response);
            } else if (jsonObject.getString("status").equals("2") &&
                    jsonObject.getString("message").equalsIgnoreCase("Invalid Details")) {
               /* int footerCount = lstCashbookSearch.getFooterViewsCount();
                Log.e(TAG,"Footer Count 1 : " + footerCount);

                // lstCashbookSearch.removeFooterView(footerView);
                removeFooterView();
                lstCashbookSearch.addFooterView(footerViewNoMoreData);

                loadMoreFlage = true;
                FLAG_INVALID_DETAIL = true;
                count++;
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Info!");
                alertDialog.setCancelable(false);
                alertDialog.setMessage(jsonObject.getString("message"));
                alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();*/
            } else {
               /* int footerCount = lstCashbookSearch.getFooterViewsCount();
                Log.e(TAG,"Footer Count 2 : " + footerCount);
                // lstCashbookSearch.removeFooterView(footerView);
                removeFooterView();
                lstCashbookSearch.addFooterView(footerViewNoMoreData);
                if (start == 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("No cash book found")
                            .setCancelable(false)
                            .setMessage(jsonObject.getString("message"))
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } else {
                    // txtNoMoreData.setVisibility(View.VISIBLE);
                    txtNoMoreData.setVisibility(View.GONE);
                }*/
            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }
    }

    public void parsing_response(String response) throws JSONException {

        //add user type data into arraylist
        addUserTypeModelsList = new ArrayList<AddUserTypeModel>();
        JSONObject objectData = new JSONObject(response);
        JSONObject data = objectData.getJSONObject("usertype");
        if(data.length()>0) {
            Iterator<String> keys = data.keys();
            int i = 0;
            while (keys.hasNext()) {
                mAddUserTypeModel = new AddUserTypeModel();
                String keyValue = (String) keys.next();
                mAddUserTypeModel.setAddUserId(keyValue);
                mAddUserTypeModel.setAddUserName(data.getString(keyValue));
                addUserTypeModelsList.add(mAddUserTypeModel);
                i++;
            }

            //display usertype into drop down
            mySpinner.setAdapter(new MyCustomAdapter(getActivity(), R.layout.spinner_row, addUserTypeModelsList));

            makeAddUserSchemeApiCalls();
        } else {
        }

    }

    //Method: get data for scheme name
    public void makeAddUserSchemeApiCalls() {

        //user scheme name thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.add_user_scheme;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app",
                            "user_type_id"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION,
                            addUserTypeModelsList.get(mySpinner.getSelectedItemPosition()).getAddUserId()
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    System.out.println("Add user scheme response1: " + response);
                    myHandlerScheme.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandlerScheme.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    // handle user scheme thread messages
    private Handler myHandlerScheme = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseSchemeSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    //parse scheme api
    public void parseSchemeSuccessResponse(String response) {

        Log.e(TAG, " AccountLedger Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = decryptAPI(encrypted_response);
                System.out.println("Add User Type: decrypted_response : " + decrypted_response);
                //Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
                parsing_scheme_response(decrypted_response);
            } else if (jsonObject.getString("status").equals("2") &&
                    jsonObject.getString("message").equalsIgnoreCase("Invalid Details")) {
               /* int footerCount = lstCashbookSearch.getFooterViewsCount();
                Log.e(TAG,"Footer Count 1 : " + footerCount);

                // lstCashbookSearch.removeFooterView(footerView);
                removeFooterView();
                lstCashbookSearch.addFooterView(footerViewNoMoreData);

                loadMoreFlage = true;
                FLAG_INVALID_DETAIL = true;
                count++;
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Info!");
                alertDialog.setCancelable(false);
                alertDialog.setMessage(jsonObject.getString("message"));
                alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();*/
            } else {
               /* int footerCount = lstCashbookSearch.getFooterViewsCount();
                Log.e(TAG,"Footer Count 2 : " + footerCount);
                // lstCashbookSearch.removeFooterView(footerView);
                removeFooterView();
                lstCashbookSearch.addFooterView(footerViewNoMoreData);
                if (start == 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("No cash book found")
                            .setCancelable(false)
                            .setMessage(jsonObject.getString("message"))
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } else {
                    // txtNoMoreData.setVisibility(View.VISIBLE);
                    txtNoMoreData.setVisibility(View.GONE);
                }*/
            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }

    }

    public void parsing_scheme_response(String response) throws JSONException {

        //add user scheme data
        userSchemeListModelArrayList = new ArrayList<UserSchemeListModel>();
        JSONArray objectData = new JSONArray(response);
        for(int i=0;i<objectData.length();i++) {
            mUserSchemeListModel = new UserSchemeListModel();
            JSONObject obj = objectData.getJSONObject(i);
            mUserSchemeListModel.setScheme_name(obj.getString("scheme_name"));
            mUserSchemeListModel.setPurchase_id(obj.getString("purchase_id"));
            mUserSchemeListModel.setAmount(obj.getString("amount"));
            mUserSchemeListModel.setScheme(obj.getString("scheme"));
            userSchemeListModelArrayList.add(mUserSchemeListModel);
        }

        //display userscheme into drop down
        spSchemeName.setAdapter(new MySchemeCustomAdapter(getActivity(), R.layout.spinner_row, userSchemeListModelArrayList));

    }

    /*Method : decryptAPI
       Decrypt response of webservice*/
    public String decryptAPI(String response) {
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, strMacAddress);
        String decrypted_response = null;
        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(mCrypt.bytesToHex(decrypted_bytes)), "UTF-8");
        }
        catch (Exception e) {
            Log.e(TAG,"Cashbook : " + "Error 7 : " + e.getMessage());
            e.printStackTrace();
        }
        return decrypted_response;
    }


    // display error in dialog
    private void displayErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        final AlertDialog alertDialog;
        try {
            alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Info!");
            alertDialog.setCancelable(false);
            alertDialog.setMessage(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        catch (Exception ex) {
            Log.e(TAG,"Error in error dialog");
            Log.e(TAG,"Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getActivity(), message);
            }
            catch (Exception e) {
                Log.e(TAG,"Error in toast message");
                Log.e(TAG,"ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    public class MyCustomAdapter extends ArrayAdapter<ArrayList<AddUserTypeModel>> {

        ArrayList<AddUserTypeModel> addUserTypeModelArrayList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<AddUserTypeModel> objects) {
            super(context, textViewResourceId, Collections.singletonList(objects));
            this.addUserTypeModelArrayList = objects;
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            return addUserTypeModelArrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.weekofday);
            //label.setText(DayOfWeek[position]);
            label.setText(addUserTypeModelArrayList.get(position).getAddUserName());

            return row;
        }
    }

    public class MySchemeCustomAdapter extends ArrayAdapter<ArrayList<UserSchemeListModel>> {

        ArrayList<UserSchemeListModel> addUserTypeModelArrayList;

        public MySchemeCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<UserSchemeListModel> objects) {
            super(context, textViewResourceId, Collections.singletonList(objects));
            this.addUserTypeModelArrayList = objects;
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            return addUserTypeModelArrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.weekofday);
            //label.setText(DayOfWeek[position]);
            label.setText(addUserTypeModelArrayList.get(position).getScheme() + " (Rs. " + addUserTypeModelArrayList.get(position).getAmount() + ") (Qty. " + addUserTypeModelArrayList.get(position).getPurchase_id() + ")");

            return row;
        }
    }

}
