package com.csl.cslibrary4a;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class ControllerConnector {
    final boolean DEBUG = false;
    boolean userDebugEnableDefault = false, userDebugEnable = userDebugEnableDefault;

    Context context; Utility utility;
    public ControllerConnector(Context context, Utility utility) {
        this.context = context;
        this.utility = utility;
    }
    private String byteArrayToString(byte[] packet) { return utility.byteArrayToString(packet); }
    private boolean compareArray(byte[] array1, byte[] array2, int length) { return utility.compareByteArray(array1, array2, length); }
    private void appendToLog(String s) { utility.appendToLog(s); }
    private void appendToLogView(String s) { utility.appendToLogView(s); }

    private int icsModel = -1;
    int getCsModel() {
        if (false) appendToLog("icsModel = " + icsModel);
        return icsModel;
    }

    public enum ControllerPayloadEvents {
        CONTROLLER_GET_VERSION, CONTROLLER_SEND_IMAGE, BLUETOOTH_SEND_IMAGE, CONTROLLER_GET_SERIALNUMBER, CONTROLLER_GET_MODELNAME, CONTROLLER_RESET
    }

    class ControllerReadData {
        ControllerPayloadEvents controllerPayloadEvents;
        byte[] dataValues;
    }

    private byte[] controllerVersion = new byte[]{-1, -1, -1};

    public String getVersion() {
        boolean DEBUG = false;
        if (controllerVersion[0] == -1) {
            boolean repeatRequest = false;
            if (controllerToWrite.size() != 0) {
                if (controllerToWrite.get(controllerToWrite.size() - 1) == ControllerPayloadEvents.CONTROLLER_GET_VERSION) {
                    repeatRequest = true;
                }
            }
            if (repeatRequest == false) {
                controllerToWrite.add(ControllerPayloadEvents.CONTROLLER_GET_VERSION);
                if (utility.DEBUG_PKDATA || DEBUG) appendToLog("PkData: add GET_VERSION to controllerWrite with length = " + controllerToWrite.size());
            }
            return "";
        } else {
            if (DEBUG) appendToLog("controllerVersion = " + byteArrayToString(controllerVersion));
            String string = String.valueOf(controllerVersion[0]) + "." + String.valueOf(controllerVersion[1]) + "." + String.valueOf(controllerVersion[2]);
            if (DEBUG) appendToLog("controllerVersion string = " + string);
            return string;
        }
    }

    byte[] image_subpart_data; int image_total_subpart, image_subpart;
    public void sendImage(boolean bIsBluetoothImage, byte[] image_subpart_data, int image_total_subpart, int image_subpart) {
        this.image_subpart_data = image_subpart_data; this.image_total_subpart = image_total_subpart; this.image_subpart = image_subpart;
        appendToLog("ConnectorController.sendImageData: buffer = " + byteArrayToString(image_subpart_data));
        controllerToWrite.add(bIsBluetoothImage ? ControllerPayloadEvents.BLUETOOTH_SEND_IMAGE : ControllerPayloadEvents.CONTROLLER_SEND_IMAGE);
    }

    private byte[] serialNumber = null;
    public String getSerialNumber() {
        if (serialNumber == null) {
            boolean repeatRequest = false;
            if (controllerToWrite.size() != 0) {
                if (controllerToWrite.get(controllerToWrite.size() - 1) == ControllerPayloadEvents.CONTROLLER_GET_SERIALNUMBER) {
                    repeatRequest = true;
                }
            }
            if (repeatRequest == false) {
                controllerToWrite.add(ControllerPayloadEvents.CONTROLLER_GET_SERIALNUMBER);
                if (utility.DEBUG_PKDATA) appendToLog("PkData: add GET_SERIALNUMBER to controllerToWrite with length = " + controllerToWrite.size());
            }
            return "";
        } else {
            byte[] bytes = new byte[serialNumber.length];
            System.arraycopy(serialNumber, 0, bytes, 0, serialNumber.length);
            if (bytes.length == 16) {
                if (bytes[15] == 0) {
                    bytes[15] = serialNumber[14];
                    bytes[14] = serialNumber[13];
                    bytes[13] = 0;
                }
                for (int i = 13; i < 16; i++) {
                    if (bytes[i] == 0) bytes[i] = 0x30;
                }
            }
            if (false) appendToLog("serialNumber = " + byteArrayToString(serialNumber) + ", revised = " + byteArrayToString(bytes));
            String string = utility.byteArray2DisplayString(bytes);
            if (string == null || string.length() == 0) {
                string = byteArrayToString(bytes);
                if (string.length() > 16) string = string.substring(0, 16);
            }
            if (false) appendToLog("string = " + string + " from serial " + byteArrayToString(serialNumber) + ", revised = " + byteArrayToString(bytes));
            return string;
        }
    }

    private byte[] modelName = null;
    public String getModelName() {
        if (false) appendToLog("modelName = " + byteArrayToString(modelName));
        String strValue = null;
        if (modelName == null) {
            boolean repeatRequest = false;
            if (controllerToWrite.size() != 0) {
                if (controllerToWrite.get(controllerToWrite.size() - 1) == ControllerPayloadEvents.CONTROLLER_GET_MODELNAME) {
                    repeatRequest = true;
                }
            }
            if (repeatRequest == false) {
                controllerToWrite.add(ControllerPayloadEvents.CONTROLLER_GET_MODELNAME);
                if (false) appendToLog("PkData: add GET_MODELNAME to controllerWrite with length = " + controllerToWrite.size());
            }
        } else {
            strValue = utility.byteArray2DisplayString(modelName);
            if (false) appendToLog("strValue 0 = " + strValue);
            if (strValue == null || strValue.length() == 0) {
                strValue = byteArrayToString(modelName).substring(0, 5);
            }
        }
        if (false) appendToLog("strValue = " + strValue);
        return strValue;
    }

    boolean resetSiliconLab() {
        boolean bRetValue = false;
        bRetValue = controllerToWrite.add(ControllerConnector.ControllerPayloadEvents.CONTROLLER_RESET);
        appendToLog("add RESET to mSiliconLabIcWrite with length = " + controllerToWrite.size());
        return bRetValue;
    }

    public ArrayList<ControllerPayloadEvents> controllerToWrite = new ArrayList<>();

    private boolean arrayTypeSet(byte[] dataBuf, int pos, ControllerPayloadEvents event) {
        boolean validEvent = false;
        appendToLog("ConnectorController.arrayTypeSet: pos = " + pos + ", event = " + event.toString() + ", dataBuf = " + byteArrayToString(dataBuf));
        switch (event) {
            case CONTROLLER_GET_VERSION:
                validEvent = true;
                break;
            case CONTROLLER_SEND_IMAGE:
                dataBuf[pos] = 1;
                validEvent = true;
                break;
            case BLUETOOTH_SEND_IMAGE:
                dataBuf[pos-1] = (byte)0xc0;
                dataBuf[pos] = 1;
                validEvent = true;
                break;
            case CONTROLLER_GET_SERIALNUMBER:
                dataBuf[pos] = 4;
                validEvent = true;
                break;
            case CONTROLLER_GET_MODELNAME:
                dataBuf[pos] = 6;
                validEvent = true;
                break;
            case CONTROLLER_RESET:
                dataBuf[pos] = 12;
                validEvent = true;
                break;
        }
        return validEvent;
    }

    private byte[] writeController(ControllerPayloadEvents event, boolean usbConnection) {
        boolean DEBUG = false;
        byte[] dataOut = null;
        if (event == ControllerPayloadEvents.CONTROLLER_GET_VERSION) {
            dataOut = new byte[]{(byte) 0xA7, (byte) 0xB3, 2, (byte) 0xE8, (byte) 0x82, (byte) 0x37, 0, 0, (byte) 0xB0, 0};
        } else if (event == ControllerPayloadEvents.CONTROLLER_SEND_IMAGE || event == ControllerPayloadEvents.BLUETOOTH_SEND_IMAGE) {
            dataOut = new byte[248];
            byte[] dataOut1 = new byte[] {(byte) 0xA7, (byte) 0xB3, (byte) 240, (byte) 0xE8, (byte) 0x82, (byte) 0x37, 0, 0,
                    (byte) 0xB0, 1, (byte)(image_total_subpart >> 8), (byte)image_total_subpart, (byte)(image_subpart >> 8), (byte)image_subpart };
            if (event == ControllerPayloadEvents.BLUETOOTH_SEND_IMAGE) dataOut1[8] = (byte) 0xC0;
            System.arraycopy(dataOut1, 0, dataOut, 0, dataOut1.length);
            for (int i = 0; i < image_subpart_data.length; i++)
            {
                dataOut[i + 14] = image_subpart_data[i];
            }
            for (int i = image_subpart_data.length; i < 234; i++)
            {
                dataOut[i + 14] = (byte)0xFF;
            }
        } else if (event == ControllerPayloadEvents.CONTROLLER_GET_SERIALNUMBER) {
            dataOut = new byte[]{(byte) 0xA7, (byte) 0xB3, 3, (byte) 0xE8, (byte) 0x82, (byte) 0x37, 0, 0, (byte) 0xB0, 4, 0};
        } else if (event == ControllerPayloadEvents.CONTROLLER_GET_MODELNAME) {
            dataOut = new byte[]{(byte) 0xA7, (byte) 0xB3, 2, (byte) 0xE8, (byte) 0x82, (byte) 0x37, 0, 0, (byte) 0xB0, 6};
        } else if (event == ControllerPayloadEvents.CONTROLLER_RESET) {
            dataOut = new byte[]{(byte) 0xA7, (byte) 0xB3, 2, (byte) 0xE8, (byte) 0x82, (byte) 0x37, 0, 0, (byte) 0xB0, 12};
        }
        if (usbConnection && dataOut != null) dataOut[1] = (byte) 0xE6;
        appendToLog("ConnectorController.writeController: sendImageData: dataOut = " + byteArrayToString(dataOut));
        if (DEBUG) appendToLog(byteArrayToString(dataOut) + " for " + event.toString());
        return dataOut;
    }
    int replyResult;
    public int getReplyResult() { return replyResult; }
    public boolean isMatchControllerToWrite(ConnectorData connectorData) {
        boolean match = false;
        if (false) appendToLog("ConnectorController.isMatchControllerToWrite: BtData, connectorData.dataValues = " + byteArrayToString(connectorData.dataValues));
        if (controllerToWrite.size() != 0 && (connectorData.dataValues[0] == (byte)0xB0 || (connectorData.dataValues[0] == (byte)0xC0 && connectorData.dataValues[1] == 1))) {
            byte[] dataInCompare = new byte[]{(byte) 0xB0, 0};
            if (arrayTypeSet(dataInCompare, 1, controllerToWrite.get(0)) && (connectorData.dataValues.length >= dataInCompare.length + 1)) {
                if (match = compareArray(connectorData.dataValues, dataInCompare, dataInCompare.length)) {
                    if (utility.DEBUG_PKDATA) appendToLog("PkData: matched Controller.Reply with payload = " + byteArrayToString(connectorData.dataValues) + " for writeData.Controller." + controllerToWrite.get(0).toString());
                    if (controllerToWrite.get(0) == ControllerPayloadEvents.CONTROLLER_GET_VERSION) {
                        if (connectorData.dataValues.length >= 2 + controllerVersion.length) {
                            System.arraycopy(connectorData.dataValues, 2, controllerVersion, 0, controllerVersion.length);
                            if (utility.DEBUG_PKDATA) appendToLog("PkData: matched Controller.Reply.GetVersion with version = " + byteArrayToString(controllerVersion));
                        }
                    } else if (controllerToWrite.get(0) == ControllerPayloadEvents.CONTROLLER_SEND_IMAGE || controllerToWrite.get(0) == ControllerPayloadEvents.BLUETOOTH_SEND_IMAGE) {
                        replyResult = connectorData.dataValues[2];
                    } else if (controllerToWrite.get(0) == ControllerPayloadEvents.CONTROLLER_GET_SERIALNUMBER) {
                        int length = connectorData.dataValues.length - 2;
                        serialNumber = new byte[length];
                        System.arraycopy(connectorData.dataValues, 2, serialNumber, 0, length);
                        if (utility.DEBUG_PKDATA) appendToLog("PkData: matched Controller.Reply.GetSerialNumber with serialNumber = " + byteArrayToString(serialNumber));
                    } else if (controllerToWrite.get(0) == ControllerPayloadEvents.CONTROLLER_GET_MODELNAME) {
                        int length = connectorData.dataValues.length - 2;
                        modelName = new byte[length];
                        System.arraycopy(connectorData.dataValues, 2, modelName, 0, length);
                        if (utility.DEBUG_PKDATA) appendToLog("PkData: matched controller.GetModelName.reply with modelName = " + byteArrayToString(modelName));
                    } else if (controllerToWrite.get(0) == ControllerPayloadEvents.CONTROLLER_RESET) {
                        replyResult = connectorData.dataValues[2];
                    } else {
                        appendToLog("matched controller.Other.reply data is found.");
                    }
                    controllerToWrite.remove(0); sendControllerToWriteSent = 0; mControllerToWriteRemoved = true;
                    if (utility.DEBUG_PKDATA) appendToLog("PkData: new controllerToWrite size = " + controllerToWrite.size());

                }
            }
        }
        return match;
    }

    public int sendControllerToWriteSent = 0; public boolean mControllerToWriteRemoved = false;
    boolean controllerFailure = false;
    public boolean isFailure() { return controllerFailure; }
    public byte[] sendControllerToWrite(boolean usbConnection) {
        if (controllerFailure) {
            controllerToWrite.remove(0); sendControllerToWriteSent = 0; mControllerToWriteRemoved = true;
        } else if (sendControllerToWriteSent >= 5) {
            int oldSize = controllerToWrite.size();
            controllerToWrite.remove(0); sendControllerToWriteSent = 0; mControllerToWriteRemoved = true;
            if (DEBUG) appendToLog("Removed after sending count-out with oldSize = " + oldSize + ", updated controllerToWrite.size() = " + controllerToWrite.size());
            if (DEBUG) appendToLog("Removed after sending count-out.");
            String string = "Problem in sending data to Controller Module. Removed data sending after count-out";
            if (userDebugEnable) Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
            else appendToLogView(string);
            controllerFailure = true; // disconnect(false);
        } else {
            if (DEBUG) appendToLog("size = " + controllerToWrite.size());
            sendControllerToWriteSent++;
            return writeController(controllerToWrite.get(0), usbConnection);
        }
        return null;
    }
}
