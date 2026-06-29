package com.csl.cs710ademoapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.csl.cs710ademoapp.MainActivity;
import com.csl.cs710ademoapp.R;
import com.csl.cslibrary4a.CustomAsyncTask;
import com.csl.cslibrary4a.CustomPopupWindow;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingUpdateFragment extends CommonFragment {
    View view;
    final boolean sameCheck = true;
    UpdateImageTaskCustom updateImageTaskCustom;
    Handler mHandler = new Handler();
    boolean updateRunning = false;
    enum ImageType {
        BLUETOOTH, CONTROLLER, RFID
    }
    ImageType imageType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_settings_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        Button button1 = (Button) view.findViewById(R.id.settingUpdateBluetoothFirmwareButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.onViewCreated.onClick: pressed button1");
                if (MainActivity.csLibrary4A.isBleConnected() == false) {
                    Toast.makeText(MainActivity.context, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    imageType = ImageType.BLUETOOTH;
                    openFilePicker();
                }
            }
        });

        Button button2 = (Button) view.findViewById(R.id.settingUpdateRFIDFirmwareButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.onViewCreated.onClick: pressed button3");
                if (MainActivity.csLibrary4A.isBleConnected() == false) {
                    Toast.makeText(MainActivity.context, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    imageType = ImageType.RFID;
                    openFilePicker();
                }
            }
        });

        Button button3 = (Button) view.findViewById(R.id.settingUpdateControllerFirmwareButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.onViewCreated.onClick: pressed button2");
                if (MainActivity.csLibrary4A.isBleConnected() == false) {
                    Toast.makeText(MainActivity.context, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    MainActivity.csLibrary4A.setTriggerReporting(false);
                    imageType = ImageType.CONTROLLER;
                    openFilePicker();
                }
            }
        });

        if (sameCheck == false) MainActivity.csLibrary4A.setSameCheck(false);
        mHandler.post(updateRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (MainActivity.csLibrary4A != null) MainActivity.csLibrary4A.setSameCheck(true);
        if (updateImageTaskCustom != null) updateImageTaskCustom.cancel(true);
        mHandler.removeCallbacks(updateRunnable);
        super.onDestroy();
    }

    public SettingUpdateFragment() {
        super("SettingUpdateFragment");
    }

    private void openFilePicker() {
        MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.openFilePicker: start");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setType("text/plain");
        intent.setType("application/octet-stream");
        filePickerLauncher.launch(intent);
        MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.openFilePicker: launched");
    }
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.filePickerLauncher: got result from startActivityForRsult with resultCode = " + result.getResultCode() + ", resultData = " + (result.getData() == null ? "null" : "valid"));
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.filePickerLauncher: no file is selected");
                    Toast.makeText(MainActivity.context, "No file is selected !!!", Toast.LENGTH_SHORT).show();
                } else {
                    Uri uri = result.getData().getData();
                    MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.filePickerLauncher: Okay filePickerLauncher result with uri = " + (uri == null ? "null" : "valid"));
                    if (uri == null) {
                        Toast.makeText(MainActivity.context, "Cannot get path from the selected file !!!", Toast.LENGTH_SHORT).show();
                    } else {
                        String string = MainActivity.csLibrary4A.getFileName4Uri(uri);
                        MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.filePickerLauncher: uri.getPath = " + string);
                        String strFileHeader = null, strFileTail = null;
                        if (imageType == ImageType.BLUETOOTH) {
                            strFileHeader = "CS710_CC2652R";
                            //strFileHeader = "CS710_CC2652R7_APP"; strFileTail = "_V1.0.14.bin";
                        } else if (imageType == ImageType.CONTROLLER) {
                            strFileHeader = "CS710ATMEL_"; strFileTail = "_V2.1.20.bin";
                        } else if (imageType == ImageType.RFID) {
                            strFileHeader = "ex10_app"; strFileTail = "2.1.2.bin.bin";
                        }
                        if (strFileHeader == null || string == null || !string.contains(strFileHeader) || !string.substring(string.length()-3).matches("bin")) {
                            Toast.makeText(MainActivity.context, "File name does not begin with " + strFileHeader, Toast.LENGTH_SHORT).show();
                        } else {
                            MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.filePickerLauncher: found uri.getPath containing " + strFileHeader);
                            InputStream inputStream = null;
                            try {
                                inputStream = MainActivity.context.getContentResolver().openInputStream(uri);
                            } catch (FileNotFoundException e) {
                                Toast.makeText(MainActivity.context, "Exception in getting input stream !!!", Toast.LENGTH_SHORT).show();
                            }
                            MainActivity.csLibrary4A.appendToLog("SettingUpdateFragment.filePickerLauncher: inputStream is " + (inputStream == null ? "null" : "valid"));
                            if (inputStream != null) {
                                updateImageTaskCustom = new UpdateImageTaskCustom(inputStream);
                                updateImageTaskCustom.execute();
                            }
                        }
                    }
                }
            });
    public class UpdateImageTaskCustom extends CustomAsyncTask {
        InputStream inputStream;
        boolean isMarkSupported;
        byte[] subpartBuffer;
        byte[][] subpartBuffers;
        byte[] crc;
        int size, total_subpart, subpart = 0;
        public UpdateImageTaskCustom(InputStream inputStream) {
            this.inputStream = inputStream;
            isMarkSupported = inputStream.markSupported();
            MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.UpdateImageTaskCustom: isMarkSupported = " + isMarkSupported);
        }

        @Override
        protected void onPreExecute() {
            int iStreamLength = -1;
            try {
                iStreamLength = inputStream.available();
            } catch (Exception ignored) { }

            MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onPreExecute: iStreamLength = " + iStreamLength);
            int iStreamLengthMin = 0, iStreamLengthMax = 0;
            if (imageType == ImageType.BLUETOOTH) {
                iStreamLengthMin = 100000; iStreamLengthMax = 150000;
            } else if (imageType == ImageType.CONTROLLER) {
                iStreamLengthMin = 350000; iStreamLengthMax = 450000;
            } else if (imageType == ImageType.RFID) {
                iStreamLengthMin = 150000; iStreamLengthMax = 250000;
            }
            if (iStreamLength > iStreamLengthMin && iStreamLength < iStreamLengthMax) {
                size = iStreamLength;
                if (imageType != ImageType.RFID) size -= 2;
                total_subpart = (int) (Math.ceil((double) iStreamLength / 234));
                MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onPreExecute: total_subpart = " + total_subpart);
                subpartBuffers = new byte[total_subpart][];
                for (int i = 0; i < total_subpart; i++) {
                    int iLen = 234;
                    if (i == total_subpart - 1) iLen = iStreamLength - 234 * i;
                    //MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onPreExecute: i = " + i + ", iLen = " + iLen);
                    subpartBuffers[i] = new byte[iLen];
                    //MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onPreExecute: 1 i = " + i + ", iLen = " + iLen);
                    try {
                        int iLenRead = inputStream.read(subpartBuffers[i], 0, subpartBuffers[i].length);
                        MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onPreExecute: i = " + i + ", iLenRead = " + iLenRead);
                    } catch (Exception ex) {  }
                }
                if (imageType != ImageType.RFID) {
                    crc = new byte[2];
                    System.arraycopy(subpartBuffers[total_subpart - 1], subpartBuffers[total_subpart - 1].length - 2, crc, 0, crc.length);
                }
                MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onPreExecute: dataToWriteSize = " + MainActivity.csLibrary4A.dataToWriteSize());
            } else {
                Toast.makeText(MainActivity.context, "Incorrect image file size. !!!", Toast.LENGTH_SHORT).show();
                MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onPreExecute: incorrect image file with iStreamLength = " + iStreamLength);
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... a) {
            String string = ""; boolean isDiconnectedShown = false;
            while (!isCancelled()) {
                if (!MainActivity.csLibrary4A.isBleConnected()) {
                    if (!isDiconnectedShown) {
                        isDiconnectedShown = true;
                        subpart--;
                        MainActivity.csLibrary4A.appendToLog(".doInBackground: link is disconnected is false with subpart = " + subpart + ", dataToWriteSize = " + MainActivity.csLibrary4A.dataToWriteSize());
                        MainActivity.csLibrary4A.connect(null);
                    }
//                    cancel(true);
//                    publishProgress(string + ", completed with failure disconnect link");
                } else {
                    if (isDiconnectedShown) {
                        isDiconnectedShown = false;
                        MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.doInBackground: link is reconnnected with subpart = " + subpart + ", dataToWriteSize = " + MainActivity.csLibrary4A.dataToWriteSize());
                    }
                    if (imageType == ImageType.BLUETOOTH && MainActivity.csLibrary4A.isBluetoothICFailure()) {
                        cancel(true);
                        publishProgress(string + ", completed with failure in sending Bluetooth image");
                    } else if (imageType == ImageType.RFID && MainActivity.csLibrary4A.isRfidFailure()) {
                        cancel(true);
                        publishProgress(string + ", completed with failure in sending RFID image");
                    } else if (imageType == ImageType.CONTROLLER && MainActivity.csLibrary4A.isControllerFailure()) {
                        cancel(true);
                        publishProgress(string + ", completed with failure in sending Controller image");
                    } else if (MainActivity.csLibrary4A.dataToWriteSize() == 0) {
                        string = String.format("Sent %d of %d", subpart, total_subpart);
                        if (subpart == 0) {
                            subpartBuffer = new byte[4];
                            if (imageType != ImageType.RFID) {
                                subpartBuffer = new byte[6];
                                subpartBuffer[4] = crc[0];
                                subpartBuffer[5] = crc[1];
                            }
                            subpartBuffer[0] = (byte) (size >> 24);
                            subpartBuffer[1] = (byte) (size >> 16);
                            subpartBuffer[2] = (byte) (size >> 8);
                            subpartBuffer[3] = (byte) (size);
                            MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.doInBackground: sendImageData: subpart = " + subpart + ", buffer = " + MainActivity.csLibrary4A.byteArrayToString(subpartBuffer));

                            if (imageType == ImageType.BLUETOOTH)
                                MainActivity.csLibrary4A.sendBluetoothIcImage(subpartBuffer, total_subpart, subpart++);
                            else if (imageType == ImageType.RFID)
                                MainActivity.csLibrary4A.sendRfidImage(subpartBuffer, total_subpart, subpart++);
                            else if (imageType == ImageType.CONTROLLER)
                                MainActivity.csLibrary4A.sendHostProcessorICImage(subpartBuffer, total_subpart, subpart++);
                            publishProgress(string);
                        } else if (subpart <= total_subpart) {
                            MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.doInBackground: subpart = " + subpart + ", buffer = " + MainActivity.csLibrary4A.byteArrayToString(subpartBuffers[subpart - 1]));
                            if (imageType == ImageType.BLUETOOTH)
                                MainActivity.csLibrary4A.sendBluetoothIcImage(subpartBuffers[subpart - 1], total_subpart, subpart++);
                            else if (imageType == ImageType.RFID)
                                MainActivity.csLibrary4A.sendRfidImage(subpartBuffers[subpart - 1], total_subpart, subpart++);
                            else if (imageType == ImageType.CONTROLLER)
                                MainActivity.csLibrary4A.sendHostProcessorICImage(subpartBuffers[subpart - 1], total_subpart, subpart++);
                            publishProgress(string);
                        } else {
                            int iReplyResult = -1;
                            if (imageType == ImageType.BLUETOOTH)
                                iReplyResult = MainActivity.csLibrary4A.getBluetoothICReplyResult();
                            else if (imageType == ImageType.RFID) {
                                iReplyResult = MainActivity.csLibrary4A.getRfidReplyResult();
                                MainActivity.csLibrary4A.disconnect(true);
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.csLibrary4A.connect(null);
                                    }
                                }, 2000);
                            } else if (imageType == ImageType.CONTROLLER)
                                iReplyResult = MainActivity.csLibrary4A.getControllerReplyResult();
                            MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.doInBackground: completed in imageType = " + imageType.toString() + ", iReplyResult = " + iReplyResult);
                            publishProgress(String.format("Sent %d of %d", total_subpart, total_subpart) + ", completed with " + (iReplyResult == 1 ? "failure " : "success ") + iReplyResult);
                            break;
                        }
                    }
                }
            }
            return "End of Asynctask()";
        }

        @Override
        protected void onProgressUpdate(String... output) {
            TextView textView = null;
            if (imageType == ImageType.BLUETOOTH) {
                textView = (TextView) view.findViewById(R.id.settingUpdateBluetoothFirmwareTextView);
            } else if (imageType == ImageType.RFID) {
                textView = (TextView) view.findViewById(R.id.settingUpdateRFIDFirmwareTextView);
            } else if (imageType == ImageType.CONTROLLER) {
                textView = (TextView) view.findViewById(R.id.settingUpdateControllerFirmwareTextView);
            }
            if (textView != null) textView.setText(output[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            boolean bValid = false;
            try {
                inputStream.close();
                bValid = true;
            } catch (Exception ignored) { }
            MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onCancelled: closing is " + bValid);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            boolean bValid = false;
            try {
                inputStream.close();
                bValid = true;
            } catch (Exception ignored) { }
            MainActivity.csLibrary4A.appendToLog("UpdateImageTaskCustom.onPostExecute: closing is " + bValid);
            String message = "!! Please Wait. Do NOT DISconnect or shut down reader for the next 10 seconds !!";
            CustomPopupWindow customPopupWindow = new CustomPopupWindow(MainActivity.context);
            customPopupWindow.setdata(false, true);
            customPopupWindow.popupStart(message, false);
        }
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            boolean updating = false;
            if (updating) {
                mHandler.postDelayed(updateRunnable, 1000);
            } else updateRunning = false;
        }
    };
}
