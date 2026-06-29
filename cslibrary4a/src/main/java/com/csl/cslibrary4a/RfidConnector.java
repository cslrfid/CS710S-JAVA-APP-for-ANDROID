package com.csl.cslibrary4a;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class RfidConnector {
    Context context; Utility utility;
    public RfidConnector(Context context, Utility utility) {
        this.context = context;
        this.utility = utility;
    }
    private String byteArrayToString(byte[] packet) { return utility.byteArrayToString(packet); }
    private boolean compareArray(byte[] array1, byte[] array2, int length) { return utility.compareByteArray(array1, array2, length); }
    private void appendToLog(String s) { utility.appendToLog(s); }
    private void appendToLogView(String s) { utility.appendToLogView(s); }

    public enum RfidPayloadEvents {
        RFID_POWER_ON, RFID_POWER_OFF, RFID_COMMAND, RFID_SEND_IMAGE,
        RFID_DATA_READ
    }
    public static class CsReaderRfidData {
        public boolean waitUplinkResponse = false;
        public boolean downlinkResponded = false;

        public boolean uplinkResponded = false;
        public boolean waitUplink1Response = false;

        public RfidPayloadEvents rfidPayloadEvent;
        public byte[] dataValues;
        public boolean invalidSequence;
        public long milliseconds;
    }

    public boolean onStatus = false; public boolean getOnStatus() { return onStatus; }

    public interface RfidConnectorCallback {
        boolean callbackMethod(byte[] dataValues);
    }
    public RfidConnectorCallback rfidConnectorCallback = null;

    public ArrayList<CsReaderRfidData> rfidToWrite = new ArrayList<>();
    public ArrayList<CsReaderRfidData> rfidToRead = new ArrayList<>();

    byte[] image_subpart_data; int image_total_subpart, image_subpart;
    public void sendImage(byte[] image_subpart_data, int image_total_subpart, int image_subpart) {
        this.image_subpart_data = image_subpart_data; this.image_total_subpart = image_total_subpart; this.image_subpart = image_subpart;
        appendToLog("ControllerRfid.sendImageData: buffer = " + byteArrayToString(image_subpart_data));
        RfidConnector.CsReaderRfidData csReaderRfidData = new RfidConnector.CsReaderRfidData();
        csReaderRfidData.rfidPayloadEvent = RfidPayloadEvents.RFID_SEND_IMAGE;
        byte[] dataOut1 = new byte[] { (byte)(image_total_subpart >> 8), (byte)image_total_subpart, (byte)(image_subpart >> 8), (byte)image_subpart };
        byte[] dataOut = new byte[238];
        System.arraycopy(dataOut1, 0, dataOut, 0, dataOut1.length);
        for (int i = 0; i < image_subpart_data.length; i++)
        {
            dataOut[i + 4] = image_subpart_data[i];
        }
        for (int i = image_subpart_data.length; i < 234; i++)
        {
            dataOut[i + 4] = (byte)0xFF;
        }
        csReaderRfidData.dataValues = dataOut;
        rfidToWrite.add(csReaderRfidData);
    }
    public boolean arrayTypeSet(byte[] dataBuf, int pos, RfidPayloadEvents event) {
        boolean validEvent = false;
        switch (event) {
            case RFID_POWER_ON:
                validEvent = true;
                break;
            case RFID_POWER_OFF:
                dataBuf[pos] = 1;
                validEvent = true;
                break;
            case RFID_COMMAND:
                dataBuf[pos] = 2;
                validEvent = true;
                break;
            case RFID_SEND_IMAGE:
                dataBuf[pos] = 3;
                validEvent = true;
                break;
        }
        return validEvent;
    }
    public byte[] writeRfid(RfidConnector.CsReaderRfidData data, boolean usbConnection) {
        boolean DEBUG = true;
        int datalength = 0;
        if (data.dataValues != null)    datalength = data.dataValues.length;
        byte[] dataOutRef = new byte[]{(byte) 0xA7, (byte) 0xB3, 2, (byte) 0xC2, (byte) 0x82, (byte) 0x37, 0, 0, (byte) 0x80, 0};
        if (usbConnection) dataOutRef[1] = (byte) 0xE6;

        byte[] dataOut = new byte[10 + datalength];
        if (datalength != 0)    {
            System.arraycopy(data.dataValues, 0, dataOut, 10, datalength);
            dataOutRef[2] += datalength;
        }
        System.arraycopy(dataOutRef, 0, dataOut, 0, dataOutRef.length);
            /*if (data.rfidPayloadEvent == RfidConnector.RfidPayloadEvents.RFID_COMMAND) {
                if (data.dataValues != null) {
                    byte[] dataOut1 = new byte[dataOut.length + data.dataValues.length];
                    System.arraycopy(dataOut, 0, dataOut1, 0, dataOut.length);
                    dataOut1[2] += data.dataValues.length;
                    System.arraycopy(data.dataValues, 0, dataOut1, dataOut.length, data.dataValues.length);
                    dataOut = dataOut1;
                }
            }*/
        appendToLog("aabb 8");
        if (arrayTypeSet(dataOut, 9, data.rfidPayloadEvent)) {
            if (utility.DEBUG_PKDATA) appendToLog(String.format("PkData: write Rfid.%s.%s with mRfidDevice.sendRfidToWriteSent = %d", data.rfidPayloadEvent.toString(), byteArrayToString(data.dataValues), sendRfidToWriteSent));
            if (sendRfidToWriteSent != 0) appendToLog("!!! mRfidDevice.sendRfidToWriteSent = " + sendRfidToWriteSent);
            appendToLog("aabb 9");
            if (DEBUG) appendToLogView("NOut: " + byteArrayToString(dataOut));
            return dataOut;
        }
        return null;
    }
    public int rfidPowerOnTimeOut = 0; //int barcodePowerOnTimeOut = 0;
    int replyResult;
    public int getReplyResult() { return replyResult; }
    public boolean isMatchRfidToWrite(ConnectorData connectorData) {
        boolean match = false, DEBUG = false;
        if (rfidToWrite.size() != 0 && connectorData.dataValues[0] == (byte)0x80) {
            byte[] dataInCompare = new byte[]{(byte) 0x80, 0};
            if (arrayTypeSet(dataInCompare, 1, rfidToWrite.get(0).rfidPayloadEvent) && (connectorData.dataValues.length == dataInCompare.length + 1)) {
                if (match = compareArray(connectorData.dataValues, dataInCompare, dataInCompare.length)) {
                    boolean bprocessed = false;
                    byte[] data1 = new byte[connectorData.dataValues.length - 2]; System.arraycopy(connectorData.dataValues, 2, data1, 0, data1.length);
                    if (utility.DEBUG_PKDATA) appendToLog("PkData: matched Rfid.Reply with payload = " + byteArrayToString(connectorData.dataValues) + " for writeData Rfid." + rfidToWrite.get(0).rfidPayloadEvent.toString() + "." + byteArrayToString(rfidToWrite.get(0).dataValues));
                    if (false && connectorData.dataValues[2] != 0) {
                        if (DEBUG) appendToLog("Rfid.reply data is found with error");
                    } else {
                        if (rfidToWrite.get(0).rfidPayloadEvent == RfidConnector.RfidPayloadEvents.RFID_POWER_ON) {
                            rfidPowerOnTimeOut = 3000;
                            onStatus = true;
                            if (utility.DEBUG_PKDATA) appendToLog("PkData: matched Rfid.Reply.PowerOn with result 0 and onStatus = " + onStatus);
                            bprocessed = true;
                        } else if (rfidToWrite.get(0).rfidPayloadEvent == RfidConnector.RfidPayloadEvents.RFID_POWER_OFF) {
                            onStatus = false;
                            if (utility.DEBUG_PKDATA) appendToLog("PkData: matched Rfid.Reply.PowerOff with result 0 and onStatus = " + onStatus);
                            bprocessed = true;
                        } else {
                            bprocessed = true;
                            if (utility.DEBUG_PKDATA) appendToLog("PkData: matched Rfid.Other.Reply data is found.");
                        }
                        replyResult = connectorData.dataValues[2];
                        appendToLog("ConnectorRfid.isMatchRfidToWrite: replyResult = " + replyResult + ", dataOut = " + byteArrayToString(rfidToWrite.get(0).dataValues));
                        RfidConnector.CsReaderRfidData csReaderRfidData = rfidToWrite.get(0);
                        if (csReaderRfidData.waitUplinkResponse) {
                            csReaderRfidData.downlinkResponded = true;
                            rfidToWrite.set(0, csReaderRfidData);
                            if (utility.DEBUG_PKDATA) appendToLog("PkData: mRfidToWrite.downlinkResponsed is set and waiting uplink data");
                            utility.writeDebug2File("Up31 " + rfidToWrite.get(0).rfidPayloadEvent.toString() + ", " + byteArrayToString(data1));
                            return true;
                        }
                        if (DEBUG) appendToLog("matched Rfid.reply data is found with mRfidToWrite.size=" + rfidToWrite.size());
                    }

                    String string = "Up31 " + (bprocessed ? "" : "Unprocessed, ") + rfidToWrite.get(0).rfidPayloadEvent.toString() + ", " + byteArrayToString(data1);
                    utility.writeDebug2File(string);
                    appendToLog("ConnectorRfid.isMatchRfidToWrite: going to rfidToWrite.remove[0] with received data = " + byteArrayToString(connectorData.dataValues));
                    rfidToWrite.remove(0); sendRfidToWriteSent = 0; mRfidToWriteRemoved = true; if (DEBUG) appendToLog("mmRfidToWrite remove 1 with remained write size = " + rfidToWrite.size());
                    appendToLog("RfidConnector.isMatchRfidToWrite, UsbData: remove one");
                    if (utility.DEBUG_PKDATA) appendToLog("PkData: new mRfidToWrite size = " + rfidToWrite.size());
                    /*if (false) {
                        for (int i = 0; i < rfidReaderChip.mRfidReaderChip.mRx000ToRead.size(); i++) {
                            if (rfidReaderChip.mRfidReaderChip.mRx000ToRead.get(i).responseType == Cs710Library4A.HostCmdResponseTypes.TYPE_COMMAND_END)
                                if (DEBUG) appendToLog("mRx0000ToRead with COMMAND_END is removed");
                        }
                        if (DEBUG) appendToLog("mRx000ToRead.clear !!!");
                    }
                    rfidReaderChip.mRfidReaderChip.mRx000ToRead.clear(); if (DEBUG) appendToLog("mRx000ToRead.clear !!!");*/
                }
            }
        }
        return match;
    }

    public int sendRfidToWriteSent = 0; public boolean mRfidToWriteRemoved = false;
    public boolean rfidFailure = false; public boolean rfidValid = false;
    public byte[] sendRfidToWrite(boolean usbConnection) {
        boolean DEBUG = false;
        boolean bValue = false;
        //if (DEBUG) appendToLog("Timeout: btSendTimeOut = " + btSendTimeOut);
        RfidConnector.RfidPayloadEvents rfidPayloadEvents = rfidToWrite.get(0).rfidPayloadEvent;
        int sendRfidToWriteSentMax = 5;
        if (rfidPayloadEvents == RfidConnector.RfidPayloadEvents.RFID_COMMAND /*&& mRfidToWrite.get(0).dataValues[0] == 0x40*/) sendRfidToWriteSentMax = 5;
        appendToLog("aabb 6");
        if (sendRfidToWriteSent >= sendRfidToWriteSentMax) {
            appendToLog("ConnectorRfid.sendRfidToWrite: going to rfidToWrite[0]");
            rfidToWrite.remove(0); sendRfidToWriteSent = 0; mRfidToWriteRemoved = true; if (DEBUG) appendToLog("mmRfidToWrite remove 2");
            if (DEBUG) appendToLog("Removed after sending count-out.");
            if (true) {
                appendToLog("Rfdid data transmission failure !!! clear mRfidToWrite buffer !!!");
                //utility.writeDebug2File("Down fails to transmit " + byteArrayToString(mRfidToWrite.get(0).dataValues));
                //appendToLog("BtDataOut: sendRfidToWrite 1 set rfidFailure as true with dataValues as " + byteArrayToString(mRfidToWrite.get(0).dataValues));
                rfidFailure = true; appendToLog("BtDataOut: rfidFailure 1");
                rfidToWrite.clear(); appendToLog("BtDataOut: mRfidToWrite.clear 6");
            } else if (rfidValid == false) {
                Toast.makeText(context, "Problem in sending data to Rfid Module. Rfid is disabled.", Toast.LENGTH_SHORT).show();
                appendToLog("BtDataOut: sendRfidToWrite 2 set rfidFailure as true");
                rfidFailure = true; appendToLog("BtDataOut: rfidFailure 2");
            } /*else {
                Toast.makeText(context, "Problem in Sending Commands to RFID Module.  Bluetooth Disconnected.  Please Reconnect", Toast.LENGTH_SHORT).show();
                appendToLog("disconnect d");
                disconnect();
            }*/
            if (DEBUG) appendToLog("done");
        } else {
            if (DEBUG)
                appendToLog("BtDataOut: size = " + rfidToWrite.size() + ", PayloadEvents = " + rfidPayloadEvents.toString() + ", data=" + byteArrayToString(rfidToWrite.get(0).dataValues));
            appendToLog("aabb 7");
            boolean retValue = false;
            return writeRfid(rfidToWrite.get(0), usbConnection);
        }
        return null;
    }

    public int invalidUpdata;
    public boolean isRfidToRead(ConnectorData connectorData) {
        boolean DEBUG = false;
        boolean found = false;
        if (connectorData.dataValues[0] == (byte) 0x81) {
            appendToLog("BtData: RfidConnector.isRfidToRead dataValue = " + byteArrayToString(connectorData.dataValues));
            RfidConnector.CsReaderRfidData cs108RfidReadData = new RfidConnector.CsReaderRfidData();
            byte[] dataValues = new byte[connectorData.dataValues.length - 2];
            System.arraycopy(connectorData.dataValues, 2, dataValues, 0, dataValues.length);
            switch (connectorData.dataValues[1]) {
                case 0:
                    if (false) appendToLog("RfidConnector.isRfidToRead: rfidConnectorCallback is " + (rfidConnectorCallback == null ? "null" : "valid"));
                    if (rfidConnectorCallback != null) {
                        if (rfidConnectorCallback.callbackMethod(dataValues)) break;
                    }
                    cs108RfidReadData.rfidPayloadEvent = RfidConnector.RfidPayloadEvents.RFID_DATA_READ;
                    cs108RfidReadData.dataValues = dataValues;
                    cs108RfidReadData.invalidSequence = connectorData.invalidSequence;
                    cs108RfidReadData.milliseconds = connectorData.milliseconds;
                    rfidToRead.add(cs108RfidReadData);
                    if (utility.DEBUG_PKDATA || true) appendToLog("PkData: Got Rfid.Uplink.DataRead with updated mRfidToRead data as " + byteArrayToString(dataValues));
                    found = true;
                    break;
                default:
                    invalidUpdata++;
                    if (utility.DEBUG_PKDATA) appendToLog("PkData: !!! found INVALID Rfid.Uplink with payload = " + byteArrayToString(connectorData.dataValues));
                    break;
            }
            if (found) {
                String string = "Up32 " + (found ? "" : "Unprocessed, ") + cs108RfidReadData.rfidPayloadEvent.toString() + ", " + byteArrayToString(dataValues);
                utility.writeDebug2File(string);
            }
        }
        return found;
    }
}
