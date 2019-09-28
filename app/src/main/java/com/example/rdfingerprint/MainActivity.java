package com.example.rdfingerprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.jetbrains.annotations.NotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;


import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private Button mfsRD, iritek, startek;
    private EditText edtAadhar;
    private ProgressDialog dialog;



    //requestData variables
    String certificateIdentifier, dataType, dc, dpId, encHmac, mc, mid, rdId, rdVer, secure_pid, sessionKey, aadharNumber;
    String errInfo;


    public static final String CAPTURE = "in.gov.uidai.rdservice.iris.CAPTURE";
    int checkDevice;


    private static final int REQ_CAPTURE = 11;
    private static final int REQ_INFO = 12;
    public static final String IRISHIELD_RD_SERVICE_PACKAGE_NAME = "com.iritech.rdservice";
    public static final String IRISHIELD_RD_SERVICE_NAME = "com.iritech.rdservice.irishield.IriShieldRDActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edtAadhar = findViewById(R.id.edttxtaadhar);

        mfsRD = findViewById(R.id.button_MFS);
        iritek = findViewById(R.id.button_iritech);
        startek = findViewById(R.id.button_startech);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        String icount = "1";
        String posh = "UNKNOWN";
        String pCount = "0";
        String timeout = "30000"; //milliseconds  wadh=Qks7UygOsvuP4j+JtIJgHGZ5qksBAJo8Q9J5gKloQlo=
        String environment = "P";
        final String pid_Options = "<PidOptions ver=\"1.0\">"
                + "<Opts fCount=\"1\" fType=\"0\" env=\"P\" format=\"0\" pidVer=\"2.0\" "
                + "timeout=\"" + timeout + "\" otp=\"\" wadh=\"\"/>"
                + "<Demo></Demo><CustOpts></CustOpts><Bios></Bios></PidOptions>\n";

        final String pidOptionXml = "<PidOptions ver=\"1.0\"><Opts fCount=\"\" fType=\"\" "
                + "iCount=\"" + icount + "\" iType=\"0\" "
                + "pCount=\"" + pCount + "\" " + (("1".equals(pCount)) ? "pType=\"0\" " : "pType=\"\" ")
                + "format=\"0\" pidVer=\"2.0\" "
                + "timeout=\"" + timeout + "\" otp=\"\" wadh=\"\" posh=\"" + posh + "\" env=\"" + environment + "\"/>"
//	            + "<Demo>"
//	            + "</Demo>"
                + "<CustOpts> "
                + "</CustOpts>"
                + "</PidOptions>";


        mfsRD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makeReqValuesNull();
                    checkDevice=0;
                    aadharNumber = edtAadhar.getText().toString();
                    boolean result = Veroeff.validateVerhoeff(aadharNumber);
                    String validationResult = String.valueOf(result);
//                    System.out.println("Aadhar verify : "+validationResult);
                    if (validationResult == "true") {
                        Intent intent2 = new Intent();
                        intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                        intent2.putExtra("PID_OPTIONS", pid_Options);
                        startActivityForResult(intent2, 2);
                    } else {
                        showToast("Invalid aadhaar number");
                    }


                } catch (ActivityNotFoundException anf) {
                    showToast("please install Mantra RD service  ");

                }

            }
        });
        iritek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makeReqValuesNull();
                    checkDevice = 1;
                    aadharNumber = edtAadhar.getText().toString();
                    boolean result = Veroeff.validateVerhoeff(aadharNumber);
                    String validationResult = String.valueOf(result);
//                    System.out.println("Aadhar verify : "+validationResult);
                    if (validationResult == "true") {
                        Intent intent = new Intent(CAPTURE);
                        intent.setClassName(IRISHIELD_RD_SERVICE_PACKAGE_NAME, IRISHIELD_RD_SERVICE_NAME);
                        intent.putExtra("PID_OPTIONS", pidOptionXml);
                        startActivityForResult(intent, REQ_CAPTURE);
                    } else {
                        showToast("Invalid aadhaar number");
                    }
                } catch (ActivityNotFoundException anf) {
                    showToast("please install Iritek RD service  ");
                }

            }
        });
        startek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makeReqValuesNull();
                    checkDevice=0;
                    aadharNumber = edtAadhar.getText().toString();
                    boolean result = Veroeff.validateVerhoeff(aadharNumber);
                    String validationResult = String.valueOf(result);
//                    System.out.println("Aadhar verify : "+validationResult);
                    if (validationResult == "true") {
                        Intent intent2 = new Intent();
                        intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                        intent2.putExtra("PID_OPTIONS", pid_Options);
                        startActivityForResult(intent2, 2);
                    } else {
                        showToast("Invalid aadhaar number");
                    }
                } catch (ActivityNotFoundException anf) {
                    showToast("please install startek RD service  ");
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("DEVICE_INFO");
                            String rdService = data.getStringExtra("RD_SERVICE_INFO");
                            String display = "";
                            if (rdService != null) {
                                display = "RD Service Info :\n" + rdService + "\n\n";
                            }
                            if (result != null) {

                                display += "Device Info :\n" + result;


//                                showToast(display);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error while deserialze device info", e);
                    }
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("PID_DATA");
                            System.out.println("result : " + result);


                            readParse(result);

                            if (result != null) {
                                /*pidData = serializer.read(PidData.class, result);
                                setText(result);*/
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error while deserialze pid data", e);
                    }
                }
                break;

            case REQ_INFO:
                if (resultCode == Activity.RESULT_OK) {
                    String deviceInfo = data.getStringExtra("DEVICE_INFO");
                    if (deviceInfo != null) {
                        if (deviceInfo.isEmpty() || deviceInfo.equals("")) {
                            showToast("Get Device Info failed!");
                        } else {
                            try {
                                Toast.makeText(getApplicationContext(), deviceInfo, Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Process failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                        return;
                    } else {
                        showToast("Can't open device. Please check plugin device  !");
                    }
                }
                break;
            case REQ_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    String pidDataXML = data.getStringExtra("PID_DATA");
                    if (pidDataXML != null) {
                        if (pidDataXML.isEmpty() || pidDataXML.equals("")) {
                            Toast.makeText(getApplicationContext(), "Capture failed!", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            try {
                                System.out.println(pidDataXML);
                                readParse(pidDataXML);
                                DocumentBuilderFactory db =
                                        DocumentBuilderFactory.newInstance();
                                Document
                                        inputDocument =
                                        db.newDocumentBuilder().parse(new InputSource(new
                                                StringReader(pidDataXML)));
                                NodeList nodes =
                                        inputDocument.getElementsByTagName("PidData");
                                if (nodes != null) {
                                    Element element = (Element) nodes.item(0);
                                    NodeList respNode =
                                            inputDocument.getElementsByTagName("Resp");
                                    if (respNode != null) {
                                        Element error = (Element) respNode.item(0);
                                        String errCode = error.getAttribute("errCode");
                                        errInfo = error.getAttribute("errInfo");
                                        if (errCode.equals("0")) {

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Capture error: " + errCode + " - " + errInfo, Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Process failed!", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    public void showToast(final String toast) {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Response");

        alert.setMessage(toast);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        alert.show();

    }


    //Parsing

    public void readParse(String str) {
        try {
            aadharNumber = edtAadhar.getText().toString();

            DocumentBuilderFactory db =
                    DocumentBuilderFactory.newInstance();
            Document
                    inputDocument =
                    db.newDocumentBuilder().parse(new InputSource(new
                            StringReader(str)));
            NodeList nodes =
                    inputDocument.getElementsByTagName("PidData");
            if (nodes != null) {

                NodeList respNode =
                        inputDocument.getElementsByTagName("DeviceInfo");


                NodeList respNode1 =
                        inputDocument.getElementsByTagName("Hmac");

                NodeList respNode2 =
                        inputDocument.getElementsByTagName("Skey");


                if (respNode != null) {
                    Element elementValues = (Element) respNode.item(0);


                    Element certIdentifier = (Element) respNode2.item(0);


                    try {
                        System.out.println("loop started");
                        for (int i = 0; i <= nodes.getLength(); i++) {

                            Node nNode = nodes.item(i);
                            System.out.println("\nCurrent Element :" + nNode.getNodeName());
                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;
                                secure_pid = eElement.getElementsByTagName("Data").item(0).getTextContent();
                                sessionKey = eElement.getElementsByTagName("Skey").item(0).getTextContent();
                                encHmac = eElement.getElementsByTagName("Hmac").item(0).getTextContent();

                                dc = elementValues.getAttribute("dc");
                                dpId = elementValues.getAttribute("dpId");
                                mc = elementValues.getAttribute("mc");
                                mid = elementValues.getAttribute("mi");
                                rdId = elementValues.getAttribute("rdsId");
                                rdVer = elementValues.getAttribute("rdsVer");
                                certificateIdentifier = certIdentifier.getAttribute("ci");

                                System.out.println("secure pid " + secure_pid.length() + "   " + secure_pid);

                            }

                        }
                    } catch (NullPointerException npe) {


                    }


                    if (certificateIdentifier != null & dc != null & dpId != null & encHmac != null & mc != null & mid != null & rdId != null & rdVer != null & secure_pid != null & sessionKey != null & aadharNumber != null) {
                        dialog= ProgressDialog.show(MainActivity.this,"Loading","Please wait.....");
                        makeNetworkRequest();
                    } else {
                        showToast("Capture failed  ");
                    }
                }


            }


        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);
        }

    }

    public void makeNetworkRequest() {




        String jsonDataForIris = "    {\n" +
                "        \"authRD\": {\n" +
                "        \"certificateIdentifier\": " + "\"" + certificateIdentifier + "\"" + ",\n" +
                "                \"dataType\": \"X\",\n" +
                "                \"dc\": " + "\"" + dc + "\"" + ",\n" +
                "                \"dpId\": " + "\"" + dpId + "\"" + ",\n" +
                "                \"encHmac\": " + "\"" + encHmac + "\"" + ",\n" +
                "                \"mc\":" + "\"" + mc + "\"" + ",\n" +
                "                \"mid\":" + "\"" + mid + "\"" + ",\n" +
                "                \"rdId\":" + "\"" + rdId + "\"" + ",\n" +
                "                \"rdVer\":" + "\"" + rdVer + "\"" + ",\n" +
                "                \"secure_pid\": " + "\"" + secure_pid + "\"" + ",\n" +
                "                \"sessionKey\": " + "\"" + sessionKey + "\"" + "\n" +
                "    },\n" +
                "        \"consent\": \"Y\",\n" +
                "            \"aUid\":" + "\"" + aadharNumber + "\"" + ",\n" +
                "                \"transtype\": \"IIR\"\n" +
                "    }";

        String jsonDataForBiomatiques ="    {\n" +
                "        \"authRD\": {\n" +
                "        \"certificateIdentifier\": " + "\"" + certificateIdentifier + "\"" + ",\n" +
                "                \"dataType\": \"X\",\n" +
                "                \"dc\": " + "\"" + dc + "\"" + ",\n" +
                "                \"dpId\": " + "\"" + dpId + "\"" + ",\n" +
                "                \"encHmac\": " + "\"" + encHmac + "\"" + ",\n" +
                "                \"mc\":" + "\"" + mc + "\"" + ",\n" +
                "                \"mid\":" + "\"" + mid + "\"" + ",\n" +
                "                \"rdId\":" + "\"" + rdId + "\"" + ",\n" +
                "                \"rdVer\":" + "\"" + rdVer + "\"" + ",\n" +
                "                \"secure_pid\": " + "\"" + secure_pid + "\"" +         ",\n" +
                "                \"sessionKey\": " + "\"" + sessionKey + "\"" + "\n" +
                "    },\n" +
                "        \"consent\": \"Y\",\n" +
                "            \"aUid\":" + "\"" + aadharNumber + "\"" + ",\n" +
                "             \"transtype\": \"FMR\"\n" +
                "    }"; ;

                if(checkDevice==1) {
                    System.out.println("framed data : " + jsonDataForIris);
                }else {
                    System.out.println("framed data : " + jsonDataForBiomatiques);
                }
//        writeToFile(jsonFormatDataForIris);


        OkHttpClient okHttpClient;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = null;

        if(checkDevice==1) {
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonDataForIris);
        }else{
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonDataForBiomatiques);
        }


        final Request request = new Request.Builder()
                .url("http://164.100.132.87/MobileAePDS2_0/eposMobileService/testauthRequest")
                .post(body)
                .build();



        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //showToast("Error response : "+e);

                //textViewResponse.setText("");
                final String myResponse = e.toString();
                JSONObject reader = null;
                try {
                    reader = new JSONObject(myResponse);

                    final String msg = reader.getString("respMessage");
                    final String code = reader.getString("respCode");
                    System.out.println("error " + e);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            showToast("Resp Msg : " + msg + "\n" + "Resp code : " + code);
                        }
                    });
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {


                final String myResponse = response.body().string();

                System.out.println("response : " + myResponse);
                try {
                    JSONObject reader = new JSONObject(myResponse);
                    final String msg = reader.getString("respMessage");
                    final String  code = reader.getString("respCode");


                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            showToast("Resp Msg : " + msg + "\n" + "Resp code : " + code);
                        }
                    });
                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        });


    }

    public void writeToFile(String data) {
        // Get the directory for the user's public pictures directory.
        String path = Environment.getExternalStorageDirectory()
                + File.separator + "request_file";
        // Create the folder.
        File folder = new File(path);
        folder.mkdirs();

        // Create the file.
        File file = new File(folder, "data.txt");
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void makeReqValuesNull(){
        certificateIdentifier=null;
        dataType=null;
        dc=null;
        dpId=null;
        encHmac=null;
        mc=null;
        mid=null;
        rdId=null;
        rdVer=null;
        secure_pid=null;
        sessionKey=null;
        aadharNumber=null;
    }


}

