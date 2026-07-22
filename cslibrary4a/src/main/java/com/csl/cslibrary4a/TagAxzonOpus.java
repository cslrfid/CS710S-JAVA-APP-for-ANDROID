package com.csl.cslibrary4a;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import java.time.Instant;

public class TagAxzonOpus {
    public static SelectData selectData;
    TagBanks tagBanks;
    String TAG = "Hello";

    public final float fNO_SUCH_SETTING = 65522;
    public final int iNO_SUCH_SETTING = 65522;
    Context context; CsReaderConnector csReaderConnector; RfidReader rfidReader; Utility utility;
    CustomMediaPlayer playerN, playerO;
    Button buttonRead, buttonWrite;
    public TagAxzonOpus(Context context, CsReaderConnector csReaderConnector, CustomMediaPlayer playerN, CustomMediaPlayer playerO, Button buttonRead, Button buttonWrite) {
        this.context = context; this.csReaderConnector = csReaderConnector;
        rfidReader = csReaderConnector.rfidReader; utility = csReaderConnector.utility;
        this.playerN = playerN; this.playerO = playerO;
        this.buttonRead = buttonRead; this.buttonWrite = buttonWrite;
        tagBanks = new TagBanks();
    }
    public CustomAsyncTask.Status getReadWriteStatus() {
        if (updateRunning) return CustomAsyncTask.Status.RUNNING;
        else if (accessTask == null) return null;
        else {
            CustomAsyncTask.Status status = accessTask.getStatus();
            if (status == CustomAsyncTask.Status.FINISHED) accessTask = null;
            return status;
        }
    }

    TagBanks.AccessData accessData;
    Handler handler = new Handler();
    AccessTaskCustom accessTask;
    void setBankDataStart(SelectData selectData, int accBank, int accOffset, int accSize, String writeData) {
        this.selectData = selectData;
        accessData = new TagBanks.AccessData(); accessData.accBank = accBank; accessData.accOffset = accOffset; accessData.accSize = accSize; accessData.data = writeData;
        handler.removeCallbacks(updateRunnable);
        handler.post(updateRunnable); updateRunning = true;
        appendToLog("TagAxzonOpus.setBankDataStart: updateRunning = true with bSelectBAP = " + selectData.bSelectBAP);
    }
    String[] stringsEpc, stringsTid, stringsUser;
    boolean updateRunning = false;
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            boolean rerunRequest = false; boolean taskRequest = false;
            if (accessTask == null) {
                appendToLog("TagBanks.updateRunnable: NULL accessReadWriteTask");
                taskRequest = true;
            } else if (accessTask.getStatus() != CustomAsyncTask.Status.FINISHED) {
                appendToLog("TagBanks.updateRunnable: accessReadWriteTask.getStatus() = " + accessTask.getStatus().toString());
                rerunRequest = true;
            } else {
                appendToLog("TagBanks.updateRunnable: FINISHED accessReadWriteTask");
                //taskRequest = true;
            }

            if (taskRequest) {
                boolean invalid = processTickItems();
                appendToLog("TagBanks.updateRunnable: processTickItems is invalid = " + invalid);
                accessTask = new AccessTaskCustom((accessData.data == null ? buttonRead : buttonWrite), invalid, true,
                        selectData, (accessData.data == null ? RfidReaderChipData.HostCommands.CMD_18K6CREAD: RfidReaderChipData.HostCommands.CMD_18K6CWRITE),
                        0, 0, true, false,
                        null, null, null, null, null, null,
                        context, csReaderConnector, playerN, playerO);
                accessTask.execute();
                rerunRequest = true;
                appendToLog("TagBanks.updateRunnable: accessTask is created with accessBank = " + accessData.accBank + ", accessOffset = " + accessData.accOffset + ", accessSize = " + accessData.accSize + ", accessData = " + (accessData.data == null ? "null" : accessData.data));
            } else if (!rerunRequest) {
                processResult();
                //rerunRequest = true;
                appendToLog("TagBanks.updateRunnable: processResult is TRUE");
            }
            if (rerunRequest) {
                handler.postDelayed(updateRunnable, 500); updateRunning = true;
                appendToLog("TagAxzonOpus.updateRunnable.run: updateRunning = true");
                appendToLog("TagBanks.updateRunnable: Restart");
            } else {
                updateRunning = false;
                appendToLog("TagAxzonOpus.updateRunnable.run: updateRunning = false");
            }
            appendToLog("TagBanks.updateRunnable: Ending with updateRunning = " + updateRunning);
        }
    };
    boolean processTickItems() {
        boolean invalidRequest1 = false;
        int accBank = 0, accOffset = 0, accSize = 0;
        String writeData = null;

        if (selectData.selectMaskEpc == null || selectData.selectMaskEpc.isEmpty()) invalidRequest1 = true;
        else  if (accessData != null) {
            accBank = accessData.accBank; accOffset = accessData.accOffset; accSize = accessData.accSize; writeData = accessData.data;
        } else {
            invalidRequest1 = true;
        }

        if (invalidRequest1 == false) {
            if (!rfidReader.setAccessBank(accBank)) {
                invalidRequest1 = true;
            }
            utility.appendToLog("TagBanks.processTickItems: bank = " + accBank + ", invalidRequest1 is " + invalidRequest1);
        }
        if (invalidRequest1 == false) {
            if (!rfidReader.setAccessOffset(accOffset)) {
                invalidRequest1 = true;
            }
            utility.appendToLog("TagBanks.processTickItems: offset = " + accOffset + ", invalidRequest1 is " + invalidRequest1);
        }
        if (invalidRequest1 == false) {
            if (accSize == 0) {
                invalidRequest1 = true;
            } else if (!rfidReader.setAccessCount(accSize)) {
                invalidRequest1 = true;
            }
            utility.appendToLog("TagBanks.processTickItems: size = " + accOffset + ", invalidRequest1 is " + invalidRequest1);
        }
        if (invalidRequest1 == false && writeData != null) {
            if (invalidRequest1 == false) {
                if (!rfidReader.setAccessWriteData(writeData)) {
                    invalidRequest1 = true;
                }
            }
            utility.appendToLog("TagBanks.processTickItems: data + " + accessData + ", invalidRequest1 is " + invalidRequest1);
        }
        return invalidRequest1;
    }
    void processResult() {
        String accessResult = null;
        /*if (accessTask == null) {
            appendToLog("TagBanks.processResult: accesssTask is NULL");
            return false;
        } else if (accessTask.getStatus() != CustomAsyncTask.Status.FINISHED) {
            appendToLog("TagBanks.processResult: accesssTask is working with status as " + accessTask.getStatus().toString());
            return false;
        } else*/ {
            accessResult = accessTask.accessResult;
            if (accessResult == null) {
                appendToLog("TagBanks.processResult: accessTask is finished with null accessResult with resultError = " + accessTask.resultError);
                if (true) {
                    //textViewLoggingInterval.setText("E");
                    //textViewLoggingInterval.setChecked(false);
                }
            } else {
                appendToLog("TagBanks.processResult: accessTask is finished with accessResult = " + accessResult + ", resultError = " + accessTask.resultError);
                if (true) {
                    //textViewLoggingInterval.setText("O");
                    //textViewLoggingInterval.setChecked(false);
                    //readWriteTypes = ReadWriteTypes.NULL;
                    int iOffset = accessData.accOffset;
                    if (accessData.data == null) {
                        switch (accessData.accBank) {
                            case 0:
                                break;
                            case 1:
                                appendToLog("TagBanks.processResult: Old stringsEpc.length = " + (stringsEpc == null ? "null" : stringsEpc.length));
                                if (stringsEpc == null || stringsEpc.length < (iOffset + accessData.accSize)) {
                                    String[] stringsNew = new String[iOffset + accessData.accSize];
                                    if (stringsEpc != null) {
                                        for (int i = 0; i < stringsEpc.length; i++) stringsNew[i] = stringsEpc[i];
                                    }
                                    stringsEpc = stringsNew;
                                }
                                appendToLog("TagBanks.processResult: New stringsEpc.length = " + (stringsEpc == null ? "null" : stringsEpc.length));
                                break;
                            case 2:
                                appendToLog("TagAxzonOpus.processResult: accessData.offset = " + accessData.accOffset + ", accessData.accSize = " + accessData.accSize);
                                appendToLog("TagBanks.processResult: Old stringsTid.length = " + (stringsTid == null ? "null" : stringsTid.length));
                                if (stringsTid == null || stringsTid.length < (iOffset + accessData.accSize)) {
                                    String[] stringsNew = new String[iOffset + accessData.accSize];
                                    if (stringsTid != null) {
                                        for (int i = 0; i < stringsTid.length; i++) stringsNew[i] = stringsTid[i];
                                    }
                                    stringsTid = stringsNew;
                                }
                                appendToLog("TagBanks.processResult: New stringsTid.length = " + (stringsTid == null ? "null" : stringsTid.length));
                                break;
                            case 3:
                                appendToLog("TagBanks.processResult: Old stringsUser.length = " + (stringsUser == null ? "null" : stringsUser.length));
                                if (stringsUser == null || stringsUser.length < (iOffset + accessData.accSize)) {
                                    String[] stringsNew = new String[iOffset + accessData.accSize];
                                    if (stringsUser != null) {
                                        for (int i = 0; i < stringsUser.length; i++) stringsNew[i] = stringsUser[i];
                                    }
                                    stringsUser = stringsNew;
                                }
                                appendToLog("TagBanks.processResult: New stringsUser.length = " + (stringsUser == null ? "null" : stringsUser.length));
                                break;
                        }
                        for (int i = 0; i < accessData.accSize; i++) {
                            String string = accessResult.substring(i * 4, i * 4 + 4);
                            appendToLog("TagBanks.processResult: bank = " + accessData.accBank + ", size = " + accessData.accSize + ", offset = " + accessData.accOffset + ", i = " + i + ", string = " + string);
                            switch (accessData.accBank) {
                                case 1:
                                    stringsEpc[accessData.accOffset + i] = string;
                                    break;
                                case 2:
                                    stringsTid[accessData.accOffset + i] = string;
                                    break;
                                case 3:
                                    stringsUser[accessData.accOffset + i] = string;
                                    break;
                                default:
                                    break;
                            }

                        }
                    } else {
                        switch (accessData.accBank) {
                            case 2:
                                appendToLog("TagBanks.processResult: writeData = " + accessData.data  + ", accBank = " + accessData.accBank + ", accOffset = " + accessData.accOffset + ", accSize = " + accessData.accSize + ", stringTid = " + (stringsTid == null ? "null" : (".length = " + stringsTid.length)));
                                if (stringsTid == null || stringsTid.length < (accessData.accOffset + accessData.accSize)) {
                                    String[] strings = new String[accessData.accOffset + accessData.accSize];
                                    if (stringsTid != null) System.arraycopy(stringsTid, 0, strings, 0, stringsTid.length);
                                    stringsTid = strings;
                                }
                                for (int i = 0; i < accessData.accSize; i++) {
                                    appendToLog("TagBanks.processResult: i = " + i + ", data = " + accessData.data.substring(i * 4, i * 4 + 4));
                                    stringsTid[accessData.accOffset +  i] = accessData.data.substring(i*4, i*4+4);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            //accessTask = null;
            //return true;
        }
    }
    public static class PcAlarmTypes {
        public int tamperAlarm = 0;
        public int batteryAlarm = 0;
        public int temperatureAlarm = 0;
        public int batteryInstalled = 0;
        public int batteryConnected = 0;
        public boolean isAlarm() {
            boolean bValue = false;
            if (temperatureAlarm > 0) bValue = true;
            else if (batteryAlarm > 0) bValue = true;
            else if (tamperAlarm > 0) bValue = true;
            return bValue;
        }
    }
    PcAlarmTypes pcAlarmType = null; int iPC_backup = iNO_SUCH_SETTING;
    public PcAlarmTypes getPcAlarmType(boolean bRequest) {
        appendToLog("TagAxzonOpus.getPcAlarmType 1");
        if (true || pcAlarmType == null) {
            appendToLog("TagAxzonOpus.getPcAlarmType 2");
            int iOffset = 1;
            if (bRequest || stringsEpc == null || stringsEpc.length <= iOffset || stringsEpc[iOffset] == null) {
                setBankDataStart(selectData, 1, iOffset, 1, null);
                return null;
            }
            appendToLog("TagAxzonOpus.getPcAlarmType 3 with stringsEpc[" + iOffset + "] = " + stringsEpc[iOffset]);
            int iValue = Integer.parseInt(stringsEpc[iOffset], 16);
            appendToLog("TagAxzonOpus.getPcAlarmType 3 with iValue = " + iValue);
            pcAlarmType = new PcAlarmTypes();
            pcAlarmType.tamperAlarm = iValue & 0x1;
            pcAlarmType.batteryAlarm = iValue & 0x2;
            pcAlarmType.temperatureAlarm = iValue & 0x4;
            pcAlarmType.batteryConnected = iValue & 0x8;
            pcAlarmType.batteryInstalled = iValue & 0x10;
            appendToLog("TagAxzonOpus.getPcAlarmType 3 with tamperAlarm=" + pcAlarmType.tamperAlarm + ", batteryAlarm=" + pcAlarmType.batteryAlarm + ", temperatureAlarm=" + pcAlarmType.temperatureAlarm
                    + ", batteryConnected=" + pcAlarmType.batteryConnected + ", batteryInstalled=" + pcAlarmType.batteryInstalled);
        }
        appendToLog("TagAxzonOpus.getPcAlarmType 4 with tamperAlarm=" + pcAlarmType.tamperAlarm + ", batteryAlarm=" + pcAlarmType.batteryAlarm + ", temperatureAlarm=" + pcAlarmType.temperatureAlarm
                + ", batteryConnected=" + pcAlarmType.batteryConnected + ", batteryInstalled=" + pcAlarmType.batteryInstalled);
        return pcAlarmType;
    }
    public boolean setPcAlarmType(PcAlarmTypes pcAlarmType) {
        appendToLog("TagAxzonOpus.setPcAlarmType 3 with tamperAlarm=" + pcAlarmType.tamperAlarm + ", batteryAlarm=" + pcAlarmType.batteryAlarm + ", temperatureAlarm=" + pcAlarmType.temperatureAlarm
                + ", batteryConnected=" + pcAlarmType.batteryConnected + ", batteryInstalled=" + pcAlarmType.batteryInstalled);
        int iOffset = 1;
        if (stringsEpc == null || stringsEpc.length <= iOffset || stringsEpc[iOffset] == null) {
            appendToLog("TagAxzonOpus.setPcAlarmType with null StringsEpc[" + iOffset + "]");
            getPcAlarmType(true);
            return false;
        }
        appendToLog("TagAxzonOpus.setPcAlarmType 3 with origin PC = " + stringsEpc[iOffset]);
        int iValueTid = Integer.valueOf(stringsEpc[iOffset], 16);
        if ( ( (((iValueTid & 0x01) == 0) && pcAlarmType.tamperAlarm == 0) || (((iValueTid & 0x01) != 0) && pcAlarmType.tamperAlarm != 0) )
                && ( (((iValueTid & 0x02) == 0) && pcAlarmType.batteryAlarm == 0) || (((iValueTid & 0x02) != 0) && pcAlarmType.batteryAlarm != 0) )
                && ( (((iValueTid & 0x04) == 0) && pcAlarmType.temperatureAlarm == 0) || (((iValueTid & 0x02) != 0) && pcAlarmType.temperatureAlarm != 0) )
                && ( (((iValueTid & 0x08) == 0) && pcAlarmType.batteryConnected == 0) || (((iValueTid & 0x08) != 0) && pcAlarmType.batteryConnected != 0) )
                && ( (((iValueTid & 0x10) == 0) && pcAlarmType.batteryInstalled == 0) || (((iValueTid & 0x10) != 0) && pcAlarmType.batteryInstalled != 0) )
        ) {
            appendToLog("TagAxzonOpus.setPcAlarmType with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
            return true;
        }
        iValueTid &= ~0x01F;
        if (pcAlarmType.tamperAlarm > 0) iValueTid |= 1;
        if (pcAlarmType.batteryAlarm > 0) iValueTid |= 2;
        if (pcAlarmType.temperatureAlarm > 0) iValueTid |= 4;
        if (pcAlarmType.batteryConnected > 0) iValueTid |= 8;
        if (pcAlarmType.batteryInstalled > 0) iValueTid |= 0x10;
        String string = String.format("%04X", iValueTid);
        appendToLog("TagAxzonOpus.setPcAlarmType with string = " + string);
        setBankDataStart(selectData,1, iOffset, 1, string);
        stringsEpc[iOffset] = string;
        return false;
    }
    LoggerStateTypes loggerEpcStateType = null;
    public LoggerStateTypes getLoggerEpcStateType() {
        appendToLog("TagAxzonOpus.getLoggerEpcStateType 1");
        if (true || loggerEpcStateType == null) {
            appendToLog("TagAxzonOpus.getLoggerEpcStateType 2 with stringsEpc.length = " + (stringsEpc == null ? "null" : stringsEpc.length));
            int iOffset = 1;
            if (stringsEpc == null || stringsEpc.length < (iOffset + 1) || stringsEpc[iOffset] == null) {
                setBankDataStart(selectData, 1, iOffset, 1,null);
                return null;
            }
            appendToLog("TagAxzonOpus.getLoggerEpcStateType 3 with stringsEpc[" + iOffset + "] = " + stringsEpc[iOffset]);
            int iValue = Integer.parseInt(stringsEpc[iOffset], 16);
            appendToLog("TagAxzonOpus.getLoggerEpcStateType 3 with iValue = " + iValue);
            iValue = iValue >> 5;
            iValue &= 0x7;
            loggerEpcStateType = LoggerStateTypes.values()[iValue];
            appendToLog("TagAxzonOpus.getLoggerEpcStateType 3 with loggerStateType = " + loggerEpcStateType.toString());

            stringsEpc[iOffset] = null;
        }
        return loggerEpcStateType;
    }
    DisableEnableTypes reusableLoggerType = null;
    public DisableEnableTypes getReusableLoggerType() {
        appendToLog("TagAxzonOpus.getReusableLoggerType 1");
        if (reusableLoggerType == null) {
            appendToLog("TagAxzonOpus.getReusableLoggerType 2 with stringsEpc.length = " + (stringsEpc == null ? "null" : stringsEpc.length));
            int iOffset = 1;
            if (stringsEpc == null || stringsEpc.length < (iOffset + 1) || stringsEpc[iOffset] == null) {
                setBankDataStart(selectData, 1, iOffset, 1,null);
                return null;
            }
            appendToLog("TagAxzonOpus.getReusableLoggerType 3 with stringsEpc[" + iOffset + "] = " + stringsEpc[iOffset]);
            int iValue = Integer.parseInt(stringsEpc[iOffset], 16);
            appendToLog("TagAxzonOpus.getReusableLoggerType 3 with iValue = " + iValue);
            if ((iValue & 0x200) == 0) reusableLoggerType = DisableEnableTypes.DISABLE;
            else reusableLoggerType = DisableEnableTypes.ENABLE;
            appendToLog("TagAxzonOpus.getReusableLoggerType 3 with loggerStateType = " + reusableLoggerType.toString());
        }
        return reusableLoggerType;
    }
    public boolean setReusableLoggerType(boolean bReusableLoggerType) {
        appendToLog("TagAxzonOpus.setReusableLoggerType 1 with bReusableLoggerType = " + bReusableLoggerType);
        bReusableLoggerType = false;
        int iOffset = 1;
        if (stringsEpc == null || stringsEpc.length <= iOffset || stringsEpc[iOffset] == null) {
            appendToLog("TagAxzonOpus.setReusableLoggerType 2 with null StringsEpc[" + iOffset + "]");
            getReusableLoggerType();
            return false;
        }
        appendToLog("TagAxzonOpus.setReusableLoggerType 3 with origin PC = " + stringsEpc[iOffset]);
        int iValueTid = Integer.valueOf(stringsEpc[iOffset], 16);
        if ( (((iValueTid & 0x200) == 0) && bReusableLoggerType == false) || (((iValueTid & 0x200) != 0) && bReusableLoggerType) ) {
            appendToLog("TagAxzonOpus.setReusableLoggerType with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
            return true;
        }
        iValueTid &= ~0x200; if (bReusableLoggerType) iValueTid |= 0x200;
        String string = String.format("%04X", iValueTid);
        appendToLog("TagAxzonOpus.setReusableLoggerType with string = " + string);
        selectData.bSelectBAP = true; setBankDataStart(selectData,1, iOffset, 1, string);
        reusableLoggerType = (bReusableLoggerType ? DisableEnableTypes.ENABLE : DisableEnableTypes.DISABLE) ;
        return false;
    }
    public static class XpcAlarmTypes {
        public int armingBattery = 0;
        public int initialBatteryLowAlarm = 0;
        public boolean isAlarm() {
            boolean bValue = false;
            if (armingBattery > 0) bValue = true;
            else if (initialBatteryLowAlarm > 0) bValue = true;
            return bValue;
        }
    }
    XpcAlarmTypes XpcAlarmType = null; int iXPC_backup = iNO_SUCH_SETTING;
    public XpcAlarmTypes getXpcAlarmType(boolean bRequest) {
        appendToLog("TagAxzonOpus.getXpcAlarmType 1");
        if (true || XpcAlarmType == null) {
            appendToLog("TagAxzonOpus.getXpcAlarmType 2");
            int iOffset = 0x21;
            if (bRequest || stringsEpc == null || stringsEpc.length <= iOffset || stringsEpc[iOffset] == null) {
                setBankDataStart(selectData, 1, iOffset, 1, null);
                return null;
            }
            appendToLog("TagAxzonOpus.getXpcAlarmType 3 with stringsEpc[" + iOffset + "] = " + stringsEpc[iOffset]);
            int iValue = Integer.parseInt(stringsEpc[iOffset], 16);
            appendToLog("TagAxzonOpus.getXpcAlarmType 3 with iValue = " + iValue);

            XpcAlarmType = new XpcAlarmTypes();
            XpcAlarmType.armingBattery = iValue & 0x10;
            XpcAlarmType.initialBatteryLowAlarm = iValue & 0x800;
            appendToLog("TagAxzonOpus.getXpcAlarmType 3 with armingBatteryLowAlarm = " + XpcAlarmType.armingBattery + ", initialBatteryLowAlarm = " + XpcAlarmType.initialBatteryLowAlarm);
        }
        appendToLog("TagAxzonOpus.getXpcAlarmType 4 with armingBatteryLowAlarm = " + XpcAlarmType.armingBattery + ", initialBatteryLowAlarm = " + XpcAlarmType.initialBatteryLowAlarm);
        return XpcAlarmType;
    }
    public boolean setXpcAlarmType(XpcAlarmTypes xpcAlarmType) {
        appendToLog("TagAxzonOpus.setXpcAlarmType with armingBatteryLowAlarm = " + XpcAlarmType.armingBattery + ", initialBatteryLowAlarm = " + XpcAlarmType.initialBatteryLowAlarm);
        int iOffset = 0x21;
        if (stringsEpc == null || stringsEpc.length <= iOffset || stringsEpc[iOffset] == null) {
            appendToLog("TagAxzonOpus.setXpcAlarmType with null StringsEpc[" + iOffset + "]");
            getPcAlarmType(true);
            return false;
        }
        appendToLog("TagAxzonOpus.setXpcAlarmType 3 with origin XPC = " + stringsEpc[iOffset]);
        int iValueTid = Integer.valueOf(stringsEpc[iOffset], 16);
        if ( ( (((iValueTid & 0x10) == 0) && xpcAlarmType.armingBattery == 0) || (((iValueTid & 0x10) != 0) && xpcAlarmType.armingBattery != 0) )
                && ( (((iValueTid & 0x800) == 0) && xpcAlarmType.initialBatteryLowAlarm == 0) || (((iValueTid & 0x800) != 0) && xpcAlarmType.initialBatteryLowAlarm != 0) )
        ) {
            appendToLog("TagAxzonOpus.setXpcAlarmType with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
            return true;
        }
        int iValue = iXPC_backup & ~0x810;
        if (XpcAlarmType.armingBattery > 0) iValue |= 0x10;
        if (XpcAlarmType.initialBatteryLowAlarm > 0) iValue |= 0x800;
        String string = String.format("%04X", iValue);
        appendToLog("TagAxzonOpus.setXpcAlarmType with string = " + string);
        setBankDataStart(selectData, 1, iOffset, 1, string);
        stringsEpc[iOffset] = string;
        return false;
    }
    DisableEnableTypes simpleSensorType = null;
    public DisableEnableTypes getSimpleSensorType() {
        appendToLog("TagAxzonOpus.getSimpleSensorType 1");
        if (simpleSensorType == null) {
            appendToLog("TagAxzonOpus.getSimpleSensorType 2 with stringsEpc.length = " + (stringsEpc == null ? "null" : stringsEpc.length));
            int iOffset = 0x21;
            if (stringsEpc == null || stringsEpc.length < (iOffset + 1) || stringsEpc[iOffset] == null) {
                setBankDataStart(selectData, 1, iOffset, 1,null);
                return null;
            }
            appendToLog("TagAxzonOpus.getSimpleSensorType 3 with stringsEpc[" + iOffset + "] = " + stringsEpc[iOffset]);
            int iValue = Integer.parseInt(stringsEpc[iOffset], 16);
            appendToLog("TagAxzonOpus.getSimpleSensorType 3 with iValue = " + iValue);
            if ((iValue & 0x20) == 0) simpleSensorType = DisableEnableTypes.DISABLE;
            else simpleSensorType = DisableEnableTypes.ENABLE;
            appendToLog("TagAxzonOpus.getSimpleSensorType 3 with loggerStateType = " + simpleSensorType.toString());
        }
        return simpleSensorType;
    }
    public boolean setSimpleSensorType(boolean bSimpleSensor) {
        appendToLog("TagAxzonOpus.setSimpleSensorType 1 with bSimpleSensor = " + bSimpleSensor);
        bSimpleSensor = true;
        int iOffset = 0x21;
        if (stringsEpc == null || stringsEpc.length <= iOffset || stringsEpc[iOffset] == null) {
            appendToLog("TagAxzonOpus.setSimpleSensorType 2 with null StringsEpc[" + iOffset + "]");
            setBankDataStart(selectData, 1, iOffset, 1,null);
            return false;
        }
        appendToLog("TagAxzonOpus.setSimpleSensorType 3 with origin PC = " + stringsEpc[iOffset]);
        int iValueTid = Integer.valueOf(stringsEpc[iOffset], 16);
        if ( (((iValueTid & 0x20) == 0) && bSimpleSensor == false) || (((iValueTid & 0x20) != 0) && bSimpleSensor) ) {
            appendToLog("TagAxzonOpus.setSimpleSensorType with SAME data");
            return true;
        }
        iValueTid &= ~0x20; if (bSimpleSensor) iValueTid |= 0x20;
        String string = String.format("%04X", iValueTid);
        appendToLog("TagAxzonOpus.setSimpleSensorType with string = " + string);
        setBankDataStart(selectData,1, iOffset, 1, string);
        this.simpleSensorType = (bSimpleSensor ? DisableEnableTypes.ENABLE : DisableEnableTypes.DISABLE) ;
        return false;
    }
    public static class FingerSpotStartupEnables {
        public int samplingRegimeEnable = 0;
        public int fingerSpotStartEnable = 0;
        public int tamperDetectEnable = 0;
        public int tamperDisconnectPolarity = 0;
        public boolean isEnable() {
            boolean bValue = false;
            if (samplingRegimeEnable > 0) bValue = true;
            else if (fingerSpotStartEnable > 0) bValue = true;
            else if (tamperDetectEnable > 0) bValue = true;
            return bValue;
        }
    }
    FingerSpotStartupEnables fingerSpotStartupEnables = null;
    public FingerSpotStartupEnables getFingerSpotStartupEnables() {
        appendToLog("TagAxzonOpus.getFingerSpotStartupEnables 1");
        if (fingerSpotStartupEnables == null) {
            appendToLog("TagAxzonOpus.getFingerSpotStartupEnables 2");
            int iOffset = 8;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData8ReadStart();
                return null;
            }

            appendToLog("TagAxzonOpus.getFingerSpotStartupEnables 3 with stringsTid[" + iOffset + "] = " + stringsTid[iOffset]);
            int iValue = Integer.parseInt(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getFingerSpotStartupEnables 3 with iValue = " + iValue);

            fingerSpotStartupEnables = new FingerSpotStartupEnables();
            if ((iValue & 0x08) != 0) fingerSpotStartupEnables.fingerSpotStartEnable = 1;
            else fingerSpotStartupEnables.fingerSpotStartEnable = 0;
            if ((iValue & 0x400) != 0) fingerSpotStartupEnables.tamperDetectEnable = 1;
            else fingerSpotStartupEnables.tamperDetectEnable = 0;
            if ((iValue & 0x800) != 0) fingerSpotStartupEnables.tamperDisconnectPolarity = 1;
            else fingerSpotStartupEnables.tamperDisconnectPolarity = 0;
            if ((iValue & 0x1000) != 0) fingerSpotStartupEnables.samplingRegimeEnable = 1;
            else fingerSpotStartupEnables.samplingRegimeEnable = 0;
            appendToLog("TagAxzonOpus.getFingerSpotStartupEnables 3 with fingerSpotStartEnable = " + fingerSpotStartupEnables.fingerSpotStartEnable +
                    ", tamperDetectEnable = " + fingerSpotStartupEnables.tamperDetectEnable +
                    ", tamperDisconnectPolarity = " + fingerSpotStartupEnables.tamperDisconnectPolarity);
        }
        return fingerSpotStartupEnables;
    }
    public void setFingerSpotStartupEnables(FingerSpotStartupEnables fingerSpotStartupEnables) {
        appendToLog("TagAxzonOpus.setFingerSpotStartupEnables with input fingerSpotStartEnable = " + fingerSpotStartupEnables.fingerSpotStartEnable +
                ", tamperDetectEnable = " + fingerSpotStartupEnables.tamperDetectEnable +
                ", tamperDisconnectPolarity = " + fingerSpotStartupEnables.tamperDisconnectPolarity +
                ", samplingRegimeEnable = " + fingerSpotStartupEnables.samplingRegimeEnable);

        int iOffset = 8;
        appendToLog("TagAxzonOpus.setFingerSpotStartupEnables 3 with stringsTid[" + iOffset + "] = " + stringsTid[iOffset]);
        int iValue = Integer.parseInt(stringsTid[iOffset], 16);
        iValue &= ~0x1C08;
        if (fingerSpotStartupEnables.fingerSpotStartEnable > 0) iValue |= 0x08;
        if (fingerSpotStartupEnables.tamperDetectEnable > 0) iValue |= 0x400;
        if (fingerSpotStartupEnables.tamperDisconnectPolarity > 0) iValue |= 0x800;
        if (fingerSpotStartupEnables.samplingRegimeEnable > 0) iValue |= 0x1000;
        String string = String.format("%04X", iValue);
        appendToLog("TagAxzonOpus.setFingerSpotStartupEnables with string = " + string);

        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        this.fingerSpotStartupEnables = fingerSpotStartupEnables;
    }
    int alarmUpperLimitx16 = iNO_SUCH_SETTING;
    public int getAlarmUpperLimitx16() {
        appendToLog("TagAxzonOpus.getAlarmUpperLimitx16 1");
        if (alarmUpperLimitx16 == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getAlarmUpperLimitx16 2");
            int iOffset = 9;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData8ReadStart();
                return iNO_SUCH_SETTING;
            }

            appendToLog("TagAxzonOpus.getAlarmUpperLimitx16 3 with stringsTid[" + iOffset + "] = " + stringsTid[iOffset]);
            int iValue = Integer.valueOf(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getAlarmUpperLimitx16 3 with iValue as " + iValue);

            alarmUpperLimitx16 = iValue & 0xFFF;
            appendToLog("TagAxzonOpus.getAlarmUpperLimitx16 3 with alarmUpperLimitx16 as " + alarmUpperLimitx16);
        }
        return alarmUpperLimitx16;
    }
    public float getAlarmUpperLimit() {
        appendToLog("TagAxzonOpus.getAlarmUpperLimit 1");
        int iValue = getAlarmUpperLimitx16();
        appendToLog("TagAxzonOpus.getAlarmUpperLimit 2 with alarmUpperLimitx16 as " + iValue);
        if (iValue == iNO_SUCH_SETTING) return fNO_SUCH_SETTING;

        //iValue = 0xF08;
        iValue &= 0xFFF;
        if ((iValue & 0x800) != 0) {
            iValue |= 0xFFFFF000;
        }
        appendToLog("TagAxzonOpus.getAlarmUpperLimit 3 with alarmUpperLimitx16 as " + iValue);
        float fValue = iValue;
        fValue /= 16;
        appendToLog("TagAxzonOpus.getAlarmUpperLimit 3 with fValue as " + fValue);
        return fValue;
    }
    public void setAlarmUpperLimit(float fValue) {
        int iOffset = 9;
        String string = stringsTid[iOffset];
        appendToLog("TagAxzonOpus.setAlarmUpperLimit with input fValue = " + fValue + ", original string = " + string);

        //fValue = (float)-15.5;
        fValue *= 16;
        int iValue = (int)fValue;
        appendToLog("TagAxzonOpus.setAlarmUpperLimit with iValue 1 = " + iValue);
        iValue &= 0xFFF;
        appendToLog("TagAxzonOpus.setAlarmUpperLimit with iValue 3 = " + iValue);

        int iValueN = (string == null ? 0 : Integer.parseInt(string, 16));
        iValueN &= ~0xFFF;
        iValueN |= iValue;
        string = String.format("%04X", iValueN);
        appendToLog("TagAxzonOpus.setAlarmUpperLimit with string = " + string);

        setBankDataStart(selectData, 2, iOffset , 1, string);
        alarmUpperLimitx16 = iValueN;
    }
    int alarmUpperDelayed = iNO_SUCH_SETTING;
    public int getAlarmUpperDelayed(boolean bRequest) {
        appendToLog("TagAxzonOpus.getAlarmUpperDelayed 1");
        if (true || alarmUpperDelayed == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getAlarmUpperDelayed 2");
            int iOffset = 9;
            if (bRequest || stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null ) {
                setTidBankData8ReadStart();
                appendToLog("TagAxzonOpus.getAlarmUpperDelayed with getReadWriteStatus = " + getReadWriteStatus().toString());
                return iNO_SUCH_SETTING;
            }
            appendToLog("TagAxzonOpus.getAlarmUpperDelayed 3 with string as " + stringsTid[iOffset]);
            int iValue = Integer.valueOf(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getAlarmUpperDelayed 3 with iValue as " + iValue);

            int iValue1 = iValue >> 12;
            alarmUpperDelayed = (iValue1 & 0xF);
            appendToLog("TagAxzonOpus.getAlarmUpperDelayed 3 with alarmUpperDelayed as " + alarmUpperDelayed);
        }
        appendToLog("TagAxzonOpus.getAlarmUpperDelayed 4: alarmUpperDelayed = " + alarmUpperDelayed);
        return alarmUpperDelayed;
    }
    public boolean setAlarmUpperDelayed(int iValue) {
        appendToLog("TagAxzonOpus.setAlarmUpperDelayed with input = " + iValue);
        int iOffset = 0x09;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            appendToLog("TagAxzonOpus.setAlarmUpperDelayed with null StringsTid[" + iOffset + "]");
            getAlarmUpperDelayed(true);
            return false;
        }
        int iValueTid = Integer.valueOf(stringsTid[iOffset], 16), iValue1 = iValueTid >> 12;
        appendToLog("TagAxzonOpus.setAlarmUpperDelayed with stringsTid[" + iOffset + "] = " + stringsTid[iOffset] + ", iValue = " + iValue + ", iValue1 = " + iValue1);
        if (iValue1 == iValue) {
            appendToLog("TagAxzonOpus.setAlarmUpperDelayed with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
            return true;
        }

        int iValue2 = getAlarmUpperLimitx16() + ((iValue & 0x0F) << 12);
        String string = String.format("%04X", iValue2 & 0xFFFF);
        appendToLog("TagAxzonOpus.setAlarmUpperDelayed with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        return false;
    }
    int alarmLowerLimitx16 = iNO_SUCH_SETTING;
    public int getAlarmLowerLimitx16() {
        appendToLog("TagAxzonOpus.getAlarmLowerLimitx16 1");
        if (alarmLowerLimitx16 == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getAlarmLowerLimitx16 2");
            int iOffset = 10;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData8ReadStart();
                return iNO_SUCH_SETTING;
            }

            appendToLog("TagAxzonOpus.getAlarmLowerLimitx16 3 with stringsTid[" + iOffset + "] = " + stringsTid[iOffset]);
            int iValue = Integer.valueOf(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getAlarmLowerLimitx16 3 with iValue as " + iValue);

            alarmLowerLimitx16 = iValue & 0xFFF;
            appendToLog("TagAxzonOpus.getAlarmLowerLimitx16 3 with alarmLowerLimitx16 as " + iValue);
        }
        return alarmLowerLimitx16;
    }
    public float getAlarmLowerLimit() {
        appendToLog("TagAxzonOpus.getAlarmLowerLimit 1");
        int iValue = getAlarmLowerLimitx16();
        appendToLog("TagAxzonOpus.getAlarmLowerLimit 2 with alarmLowerLimitx16 as " + iValue);
        if (iValue == iNO_SUCH_SETTING) return fNO_SUCH_SETTING;

        //iValue = 0xF08;
        iValue &= 0xFFF;
        if ((iValue & 0x800) != 0) {
            iValue |= 0xFFFFF000;
        }
        appendToLog("TagAxzonOpus.getAlarmLowerLimit 3 with alarmUpperLimitx16 as " + iValue);
        float fValue = iValue;
        fValue /= 16;
        appendToLog("TagAxzonOpus.getAlarmLowerLimit 3 with fValue as " + fValue);
        return fValue;
    }
    public void setAlarmLowerLimit(float fValue) {
        int iOffset = 10;
        String string = stringsTid[iOffset];
        appendToLog("TagAxzonOpus.setAlarmLowerLimit with input fValue = " + fValue + ", original string = " + string + ", iTidA_Backup = " + String.format("%04X", iTidA_backup));

        //fValue = (float)-15.5;
        fValue *= 16;
        int iValue = (int)fValue;
        appendToLog("TagAxzonOpus.setAlarmLowerLimit with iValue 1 = " + iValue);
        iValue &= 0xFFF;
        appendToLog("TagAxzonOpus.setAlarmLowerLimit with iValue 3 = " + iValue);

        int iValueN = (string == null ? iTidA_backup : Integer.parseInt(string, 16));
        iValueN &= ~0xFFF;
        iValueN |= iValue;
        string = String.format("%04X", iValueN);
        appendToLog("TagAxzonOpus.setAlarmLowerLimit with string = " + string);

        setBankDataStart(selectData, 2, iOffset , 1, string);
        alarmLowerLimitx16 = iValueN;
    }
    int alarmLowerDelayed = iNO_SUCH_SETTING; int iTidA_backup = 0;
    public int getAlarmLowerDelayed(boolean bRequest) {
        appendToLog("TagAxzonOpus.getAlarmLowerDelayed 1");
        if (true || alarmLowerDelayed == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getAlarmLowerDelayed 2");
            int iOffset = 0x0A;
            if (bRequest || stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null ) {
                setTidBankData8ReadStart();
                return iNO_SUCH_SETTING;
            }
            appendToLog("TagAxzonOpus.getAlarmLowerDelayed 3 with string as " + stringsTid[iOffset]);
            int iValue = Integer.valueOf(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getAlarmLowerDelayed 3 with iValue as " + iValue);

            iValue = iValue >> 12;
            alarmLowerDelayed = (iValue & 0xF);
            appendToLog("TagAxzonOpus.getAlarmLowerDelayed 3 with alarmLowerDelayed as " + alarmLowerDelayed);
        }
        appendToLog("TagAxzonOpus.getAlarmLowerDelayed 4: alarmLowerDelayed = " + alarmLowerDelayed);
        return alarmLowerDelayed;
    }
    public boolean setAlarmLowerDelayed(int iValue) {
        appendToLog("TagAxzonOpus.setAlarmLowerDelayed with input = " + iValue);
        int iOffset = 0x0A;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            appendToLog("TagAxzonOpus.setAlarmLowerDelayed with null StringsTid[" + iOffset + "]");
            getAlarmLowerDelayed(true);
            return false;
        }
        int iValueTid = Integer.valueOf(stringsTid[iOffset], 16), iValue1 = iValueTid >>12;
        appendToLog("TagAxzonOpus.setAlarmLowerDelayed with stringsTid[" + iOffset + "] = " + stringsTid[iOffset] + ", iValue = " + iValue + ", iValue1 = " + iValue1);
        if (iValue1 == iValue) {
            appendToLog("TagAxzonOpus.setAlarmLowerDelayed with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
            return true;
        }

        int iValue2 = getAlarmUpperLimitx16() + ((iValue & 0x0F) << 12);
        String string = String.format("%04X", iValue2 & 0xFFFF);
        appendToLog("TagAxzonOpus.setAlarmLowerDelayed with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        return false;
    }
    String firstTemperatureAlarmAddress = null;
    public String getFirstTemperatureAlarmAddress() {
        appendToLog("TagAxzonOpus.getFirstTemperatureAlarmAddress 1");
        if (true || firstTemperatureAlarmAddress == null) {
            appendToLog("TagAxzonOpus.getFirstTemperatureAlarmAddress 2");
            int iOffset = 0x0b;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData8ReadStart();
                return null;
            }
            appendToLog("TagAxzonOpus.getFirstTemperatureAlarmAddress 3 with string as " + stringsTid[iOffset]);
            int iValue = Integer.valueOf(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getFirstTemperatureAlarmAddress 3 with iValue as " + iValue);
            iValue &= 0xFFF;
            firstTemperatureAlarmAddress = String.format("%03X", iValue);
            appendToLog("TagAxzonOpus.getFirstTemperatureAlarmAddress 3 with string as " + firstTemperatureAlarmAddress);
            stringsTid[iOffset] = null;
        }
        return firstTemperatureAlarmAddress;
    }
    public boolean setFirstTemperatureAlarmAddress(int iValue) {
        appendToLog("TagAxzonOpus.setFirstTemperatureAlarmAddress with input = " + iValue);
        int iOffset = 0x0b;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            appendToLog("TagAxzonOpus.setFirstTemperatureAlarmAddress with null StringsTid[" + iOffset + "]");
        } else {
            int iValueTid = Integer.valueOf(stringsTid[iOffset], 16), iValue1 = iValueTid & 0xFFF;
            if (iValue1 == iValue) {
                appendToLog("TagAxzonOpus.setFirstTemperatureAlarmAddress with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        String string = String.format("%04X", iValue & 0x0FFF);
        appendToLog("TagAxzonOpus.setFirstTemperatureAlarmAddress with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        return false;
    }
    String firstTamperAlarmAddress = null;
    public String getFirstTamperAlarmAddress() {
        appendToLog("TagAxzonOpus.getFirstTamperAlarmAddress 1");
        if (true || firstTamperAlarmAddress == null) {
            appendToLog("TagAxzonOpus.getFirstTamperAlarmAddress 2 with " + (stringsTid == null ? "stringsTid = null" : ", stringsTid.length = " + stringsTid.length + ", stringsTid[0x0c] = " + (stringsTid[12] == null ? "null" : "valid")));
            int iOffset = 0x0C;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData8ReadStart();
                return null;
            }
            appendToLog("TagAxzonOpus.getFirstTamperAlarmAddress 3 with string as " + stringsTid[iOffset]);
            int iValue = Integer.valueOf(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getFirstTamperAlarmAddress 3 with iValue as " + iValue);
            iValue &= 0xFFF;
            firstTamperAlarmAddress = String.format("%03X", iValue);
            appendToLog("TagAxzonOpus.getFirstTamperAlarmAddress 3 with string as " + firstTamperAlarmAddress);
            stringsTid[iOffset] = null;
        }
        return firstTamperAlarmAddress;
    }
    public boolean setFirstTamperAlarmAddress(int iValue) {
        appendToLog("TagAxzonOpus.setFirstTamperAlarmAddress with input = " + iValue);
        int iOffset = 0x0C;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            appendToLog("TagAxzonOpus.setFirstTamperAlarmAddress with null StringsTid[" + iOffset + "]");
        } else {
            int iValueTid = Integer.valueOf(stringsTid[iOffset], 16), iValue1 = iValueTid & 0xFFF;
            appendToLog("TagAxzonOpus.setFirstTamperAlarmAddress with stringsTid[" + iOffset + "] = " + stringsTid[iOffset] + ", iValueTid = " + iValueTid + ", iValue1 = " + iValue1);
            if (iValue1 == iValue) {
                appendToLog("TagAxzonOpus.setFirstTamperAlarmAddress with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        String string = String.format("%04X", iValue & 0x0FFF);
        appendToLog("TagAxzonOpus.setFirstTamperAlarmAddress with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        return false;
    }
    String fingerArmedClock = null;
    public String getFingerArmedClock() {
        appendToLog("TagAxzonOpus.getFingerArmedClock 1");
        if (true || fingerArmedClock == null) {
            appendToLog("TagAxzonOpus.getFingerArmedClock 2");
            int iOffset = 0x0d;
            if (stringsTid == null || stringsTid.length < (iOffset + 2) || stringsTid[iOffset] == null || stringsTid[iOffset+1] == null) {
                setTidBankData8ReadStart();
                return null;
            }
            appendToLog("TagAxzonOpus.getFingerArmedClock 3 with string as " + stringsTid[iOffset] + ", " + stringsTid[iOffset+1]);
            fingerArmedClock = stringsTid[iOffset].substring(2,4) + stringsTid[iOffset + 1];
            appendToLog("TagAxzonOpus.getFingerArmedClock 3 with iValue as " + fingerArmedClock);
            stringsTid[iOffset] = null; stringsTid[iOffset + 1] = null;
        }
        return fingerArmedClock;
    }
    public boolean setFingerArmedClock(int iValue) {
        appendToLog("TagAxzonOpus.setFingerArmedClock with input = " + iValue);
        int iOffset = 0x0d;
        if (stringsTid == null || stringsTid.length < (iOffset + 2) || stringsTid[iOffset] == null || stringsTid[iOffset+1] == null) {
            appendToLog("TagAxzonOpus.setFingerArmedClock with null StringsTid[" + iOffset + "]");
        } else {
            String string = String.format("%06X", iValue & 0xFFFFFF), stringTid = stringsTid[iOffset] + stringsTid[iOffset+1]; stringTid = stringTid.substring(2,8);
            appendToLog("TagAxzonOpus.setFingerArmedClock with string = " + string + ", stringTid = " + stringTid);
            if (stringTid.matches(string)) {
                appendToLog("TagAxzonOpus.setFingerArmedClock with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        String string = String.format("%08X", iValue & 0xFFFFFF);
        appendToLog("TagAxzonOpus.setFingerArmedClock with string = " + string);
        setBankDataStart(selectData,2, iOffset, 2, string);
        stringsTid[iOffset] = string.substring(0,4); stringsTid[iOffset+1] = string.substring(4,8);
        return false;
    }
    public enum LoggingIntervalTypes {
        INTERVAL_1SEC, INTERVAL_5SEC, INTERVAL_10SEC, INTERVAL_15SEC, INTERVAL_20SEC, INTERVAL_25SEC, INTERVAL_30SEC,
        INTERVAL_1MIN, INTERVAL_2MIN, INTERVAL_3MIN, INTERVAL_4MIN, INTERVAL_5MIN, INTERVAL_6MIN, INTERVAL_7MIN,
        INTERVAL_10MIN, INTERVAL_15MIN, INTERVAL_20MIN, INTERVAL_25MIN, INTERVAL_30MIN, INTERVAL_35MIN, INTERVAL_40MIN,
        INTERVAL_1HOUR, INTERVAL_2HOUR, INTERVAL_3HOUR, INTERVAL_4HOUR, INTERVAL_5HOUR, INTERVAL_6HOUR, INTERVAL_7HOUR, INTERVAL_8HOUR
    }
    LoggingIntervalTypes loggingIntervalType = null;
    public LoggingIntervalTypes getLoggingIntervalType() {
        appendToLog("TagAxzonOpus.getLoggingIntervalType 1");
        if (loggingIntervalType == null) {
            appendToLog("TagAxzonOpus.getLoggingIntervalType 2");
            int iOffset = 8, iOffset1 = 0x0F;
            if (stringsTid == null || stringsTid.length <= iOffset1 || stringsTid[iOffset] == null || stringsTid[iOffset1] == null) {
                setTidBankData8ReadStart();
                return null;
            }

            for (int i = iOffset; i < iOffset1 + 1; i++) {
                appendToLog("TagAxzonOpus.getLoggingIntervalType 3 with stringsTid[" + i + "] = " + stringsTid[i]);
            }
            int iMsb = Integer.parseInt(stringsTid[iOffset], 16) & 0x100;
            int iLsb = (Integer.parseInt(stringsTid[iOffset1], 16) >> 3) & 0x0F;
            appendToLog("TagAxzonOpus.getLoggingIntervalType 3 with iMsb = " + iMsb + ", iLsb = " + iLsb);
            loggingIntervalType = null;
            if (iMsb != 0) {
                if (iLsb >= 0 && iLsb <= 10) {
                    loggingIntervalType = LoggingIntervalTypes.values()[iLsb];
                } else if (iLsb >= 0x0B && iLsb <= 0x0C) {
                    loggingIntervalType = LoggingIntervalTypes.values()[12 + iLsb - 0x0B];
                }
            } else {
                if (iLsb == 0) loggingIntervalType = LoggingIntervalTypes.INTERVAL_5MIN; //Okay
                else if (iLsb >= 1) {
                    loggingIntervalType = LoggingIntervalTypes.values()[14 + iLsb - 1];
                }
            }
        }
        return loggingIntervalType;
    }
    public void setLoggingIntervalType(LoggingIntervalTypes loggingIntervalType) {
        appendToLog("TagAxzonOpus.setLoggingIntervalType with input = " + loggingIntervalType.toString() + ", stringsTid.length = " + stringsTid.length);
        int iOffset = 8, iOffset1 = 0x0F;
        String[] strings = new String[iOffset1-iOffset+1];
        System.arraycopy(stringsTid, iOffset, strings, 0, strings.length);
        for (int i = 0; i < strings.length; i++) {
            appendToLog("TagAxzonOpus.setLoggingIntervalType with strings[" + i + "] = " + strings[i]);
        }

        boolean bMsbExpected = (loggingIntervalType.ordinal() <= LoggingIntervalTypes.INTERVAL_7MIN.ordinal());
        int iValueExpected = -1;
        if (bMsbExpected) {
            if (loggingIntervalType == LoggingIntervalTypes.INTERVAL_5MIN) {
                bMsbExpected = false;
                iValueExpected = 0;
            } else if (loggingIntervalType.ordinal() > LoggingIntervalTypes.INTERVAL_5MIN.ordinal() )
                iValueExpected = loggingIntervalType.ordinal() - 1;
            else iValueExpected = loggingIntervalType.ordinal();
        } else iValueExpected = loggingIntervalType.ordinal() - LoggingIntervalTypes.INTERVAL_7MIN.ordinal();
        appendToLog("TagAxzonOpus.setLoggingIntervalType with bMsbExpected = " + bMsbExpected + ", iValueExpected = " + iValueExpected);

        int iValueH = Integer.parseInt(strings[8-iOffset], 16);
        if (bMsbExpected) iValueH |= 0x100;
        else iValueH &= ~0x100;
        strings[8-iOffset] = String.format("%04X", iValueH);
        int iValueL = Integer.parseInt(strings[0x0F-iOffset], 16);
        iValueL &= ~0x78;
        iValueL |= (iValueExpected << 3);
        strings[0x0F-iOffset] = String.format("%04X", iValueL);

        StringBuilder stringData = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            stringData.append(strings[i]);
            appendToLog("TagAxzonOpus.setLoggingIntervalType with strings[" + i + "] = " + strings[i] + ", stringData = " + stringData.toString());
        }
        setBankDataStart(selectData, 2, 8 , strings.length, stringData.toString());
        this.loggingIntervalType =  null;
    }
    int samplingRegimePeriod = iNO_SUCH_SETTING;
    public int getSamplingRegimePeriod() {
        appendToLog("TagAxzonOpus.getSamplingRegimePeriod 1");
        if (samplingRegimePeriod == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getSamplingRegimePeriod 2");
            int iOffset = 0xF;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null ) {
                setTidBankData8ReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getSamplingRegimePeriod 3 with string as " + stringsTid[iOffset]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getSamplingRegimePeriod 3 with iValue as " + iValue);

                samplingRegimePeriod = iValue;
                appendToLog("TagAxzonOpus.getSamplingRegimePeriod 3 with samplingRegimePeriod as " + samplingRegimePeriod);
            }
        }
        appendToLog("TagAxzonOpus.getSamplingRegimePeriod 4: samplingRegimePeriod = " + samplingRegimePeriod);
        return samplingRegimePeriod;
    }
    public void setSamplingRegimePeriod(int iValue) {
        appendToLog("TagAxzonOpus.setSamplingRegimePeriod with input iValue = " + iValue);

        String string = String.format("%04X", iValue);
        appendToLog("TagAxzonOpus.setSamplingRegimePeriod with string = " + string);

        int iOffset = 0x0F;
        setBankDataStart(selectData, 2, iOffset , 1, string);
        samplingRegimePeriod = iValue;
    }
    public static class TidAlarmTypes {
        public int lowTemperatureAlarm = 0;
        public int highTemperatureAlarm = 0;
        public int tamperAlarm = 0;
        public int batteryAlarm = 0;
        public boolean isAlarm() {
            boolean bValue = false;
            if (lowTemperatureAlarm > 0) bValue = true;
            else if (highTemperatureAlarm > 0) bValue = true;
            else if (tamperAlarm > 0) bValue = true;
            else if (batteryAlarm > 0) bValue = true;
            return bValue;
        }
    }
    TidAlarmTypes tidAlarmType = null;
    public TidAlarmTypes getTidAlarmType(boolean bRequest) {
        appendToLog("TagAxzonOpus.getTidAlarmType 1");
        if (true || tidAlarmType == null) {
            appendToLog("TagAxzonOpus.getTidAlarmType 2");
            int iOffset = 0x10;
            if (bRequest || stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData8ReadStart();
                return null;
            }
            appendToLog("TagAxzonOpus.getTidAlarmType 3 with stringsTid[" + iOffset + "] = " + stringsTid[iOffset]);
            int iValue = Integer.parseInt(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getTidAlarmType 3 with iValue = " + iValue);

            iValue &= 0xF;
            tidAlarmType = new TidAlarmTypes();
            tidAlarmType.lowTemperatureAlarm = iValue & 1;
            tidAlarmType.highTemperatureAlarm = iValue & 2;
            tidAlarmType.tamperAlarm = iValue & 4;
            tidAlarmType.batteryAlarm = iValue & 8;
            appendToLog("TagAxzonOpus.getTidAlarmType 3 with lowTemperatureAlarm = " + tidAlarmType.lowTemperatureAlarm + ", highTemperatureAlarm = " + tidAlarmType.highTemperatureAlarm
                    + ", tamperAlarm = " + tidAlarmType.tamperAlarm + ", batteryAlarm = " + tidAlarmType.batteryAlarm);
        }
        appendToLog("TagAxzonOpus.getTidAlarmType 4 with lowTemperatureAlarm = " + tidAlarmType.lowTemperatureAlarm + ", highTemperatureAlarm = " + tidAlarmType.highTemperatureAlarm
                + ", tamperAlarm = " + tidAlarmType.tamperAlarm + ", batteryAlarm = " + tidAlarmType.batteryAlarm);
        return tidAlarmType;
    }
    public boolean setTidAlarmType(TidAlarmTypes tidAlarmType) {
        appendToLog("TagAxzonOpus.setTidAlarmType with lowTemperatureAlarm = " + tidAlarmType.lowTemperatureAlarm  + ", highTemperatureAlarm = " + tidAlarmType.highTemperatureAlarm + ", tamperAlarm = " + tidAlarmType.tamperAlarm + ", batteryAlarm = " + tidAlarmType.batteryAlarm);
        int iOffset = 0x10;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            appendToLog("TagAxzonOpus.setTidAlarmType with null StringsTid[" + iOffset + "]");
            getTidAlarmType(true);
            return false;
        }
        int iValueTid = Integer.valueOf(stringsTid[iOffset], 16);
        appendToLog("TagAxzonOpus.setTidAlarmType with iValueTid = " + iValueTid
                + ", " + ((iValueTid & 0x08) != 0) + "-" + (tidAlarmType.batteryAlarm != 0) + "-" + (((iValueTid & 0x08) != 0) ^ tidAlarmType.batteryAlarm != 0)
                + ", " + (((iValueTid & 0x04) != 0) ^ tidAlarmType.tamperAlarm != 0)
                + ", " + (((iValueTid & 0x02) != 0) ^ tidAlarmType.highTemperatureAlarm != 0)
                + ", " + (((iValueTid & 0x01) != 0) ^ tidAlarmType.lowTemperatureAlarm != 0)
        );
        if ( ( (((iValueTid & 0x08) == 0) && tidAlarmType.batteryAlarm == 0) || (((iValueTid & 0x08) != 0) && tidAlarmType.batteryAlarm != 0) )
                && ( (((iValueTid & 0x04) == 0) && tidAlarmType.tamperAlarm == 0) || (((iValueTid & 0x04) != 0) && tidAlarmType.tamperAlarm != 0) )
                && ( (((iValueTid & 0x02) == 0) && tidAlarmType.highTemperatureAlarm == 0) || (((iValueTid & 0x02) != 0) && tidAlarmType.highTemperatureAlarm != 0) )
                && ( (((iValueTid & 0x01) == 0) && tidAlarmType.lowTemperatureAlarm == 0) || (((iValueTid & 0x01) != 0) && tidAlarmType.lowTemperatureAlarm != 0) ) ) {
            appendToLog("TagAxzonOpus.setTidAlarmType with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
            return true;
        }

        iValueTid &= ~0xF;
        appendToLog("TagAxzonOpus.setTidAlarmType with iValueTid = " + iValueTid);
        if (tidAlarmType.batteryAlarm > 0) iValueTid |= 8;
        if (tidAlarmType.tamperAlarm > 0) iValueTid |= 4;
        if (tidAlarmType.highTemperatureAlarm > 0) iValueTid |= 2;
        if (tidAlarmType.lowTemperatureAlarm > 0) iValueTid |= 1;
        String string = String.format("%04X", iValueTid & 0x1FFF);
        appendToLog("TagAxzonOpus.setTidAlarmType with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        return false;
    }
    int alarmLowerDelay = iNO_SUCH_SETTING;
    public int getAlarmLowerDelay() {
        appendToLog("TagAxzonOpus.getAlarmLowerDelay 1");
        if (alarmLowerDelay == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getAlarmLowerDelay 2");
            int iOffset = 0x10;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null ) {
                setTidBankData8ReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getAlarmLowerDelay 3 with string as " + stringsTid[iOffset]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getAlarmLowerDelay 3 with iValue as " + iValue);

                iValue = iValue >> 4;
                alarmLowerDelay = (iValue & 0x7) + 1;
                appendToLog("TagAxzonOpus.getAlarmLowerDelay 3 with alarmLowerDelay as " + alarmLowerDelay);
            }
        }
        appendToLog("TagAxzonOpus.getAlarmLowerDelay 4: alarmLowerDelay = " + alarmLowerDelay);
        return alarmLowerDelay;
    }
    public void setAlarmLowerDelay(int iValue) {
        appendToLog("TagAxzonOpus.setAlarmLowerDelay with input iValue = " + iValue);

        iValue--;
        if (iValue < 0) iValue = 0;
        else if (iValue > 7) iValue = 7;

        int iOffset = 0x10;
        int iValue1 = Integer.valueOf(stringsTid[iOffset], 16);
        appendToLog("TagAxzonOpus.setAlarmLowerDelay 1 with iValue1 as " + String.format("%04X", iValue1));
        iValue1 &= ~0x70;
        iValue1 |= (iValue << 4);
        appendToLog("TagAxzonOpus.setAlarmLowerDelay 2 with iValue1 as " + String.format("%04X", iValue1));

        String string = String.format("%04X", iValue1);
        appendToLog("TagAxzonOpus.setAlarmLowerDelay with string = " + string);

        setBankDataStart(selectData, 2, iOffset , 1, string);
        alarmLowerDelay = iValue + 1;
    }
    int alarmUpperDelay = iNO_SUCH_SETTING;
    public int getAlarmUpperDelay() {
        appendToLog("TagAxzonOpus.getAlarmUpperDelay 1");
        if (alarmUpperDelay == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getAlarmUpperDelay 2 with "
                    + (stringsTid == null ? "stringsTid = null" :
                    ("stringsTid.length = " + stringsTid.length + ", stringsTid[0x10] = " + (stringsTid[0x10] == null ? "null" : "valid"))));
            int iOffset = 0x10;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null ) {
                setTidBankData8ReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getAlarmUpperDelay 3 with string as " + stringsTid[iOffset]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getAlarmUpperDelay 3 with iValue as " + iValue);

                iValue = iValue >> 7;
                alarmUpperDelay = (iValue & 0x7) + 1;
                appendToLog("TagAxzonOpus.getAlarmLowerDelay 3 with alarmUpperDelay as " + alarmUpperDelay);
            }
        }
        appendToLog("TagAxzonOpus.getAlarmUpperDelay 4: alarmUpperDelay = " + alarmUpperDelay);
        return alarmUpperDelay;
    }
    public void setAlarmUpperDelay(int iValue) {
        appendToLog("TagAxzonOpus.setAlarmUpperDelay with input iValue = " + iValue);

        iValue--;
        if (iValue < 0) iValue = 0;
        else if (iValue > 7) iValue = 7;

        int iOffset = 0x10;
        int iValue1 = Integer.valueOf(stringsTid[iOffset], 16);
        appendToLog("TagAxzonOpus.setAlarmUpperDelay 1 with iValue1 as " + String.format("%04X", iValue1));
        iValue1 &= ~0x380;
        iValue1 |= (iValue << 7);
        appendToLog("TagAxzonOpus.setAlarmUpperDelay 2 with iValue1 as " + String.format("%04X", iValue1));

        String string = String.format("%04X", iValue1);
        appendToLog("TagAxzonOpus.setAlarmUpperDelay with string = " + string);

        setBankDataStart(selectData, 2, iOffset , 1, string);
        alarmUpperDelay = iValue + 1;
    }
    int loggingDelayedStart = iNO_SUCH_SETTING;
    public int getLoggingDelayedStart() {
        appendToLog("TagAxzonOpus.getDelayedLoggingStart 1");
        if (loggingDelayedStart == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getDelayedLoggingStart 2");
            int iOffset = 0x10;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData8ReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getDelayedLoggingStart 3 with string as " + stringsTid[iOffset]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getDelayedLoggingStart 3 with iValue as " + iValue);

                iValue = iValue >> 10;
                loggingDelayedStart = (iValue & 0x7);
                appendToLog("TagAxzonOpus.getDelayedLoggingStart 3 with iValue as " + iValue);
            }
        }
        appendToLog("TagAxzonOpus.getDelayedLoggingStart 4: delayLoggingStart = " + loggingDelayedStart);
        return loggingDelayedStart;
    }
    public void setLoggingDelayedStart(int iValue) {
        appendToLog("TagAxzonOpus.setDelayedLoggingStart with input iValue = " + iValue);

        if (iValue < 0) iValue = 0;
        else if (iValue > 7) iValue = 7;

        int iOffset = 0x10;
        int iValue1 = Integer.valueOf(stringsTid[iOffset], 16);
        appendToLog("TagAxzonOpus.setDelayedLoggingStart 1 with iValue1 as " + String.format("%04X", iValue1));
        iValue1 &= ~0x1C00;
        iValue1 |= (iValue << 10);
        appendToLog("TagAxzonOpus.setDelayedLoggingStart 2 with iValue1 as " + String.format("%04X", iValue1));

        String string = String.format("%04X", iValue1);
        appendToLog("TagAxzonOpus.setDelayedLoggingStart with string = " + string);

        setBankDataStart(selectData, 2, iOffset , 1, string);
        loggingDelayedStart = iValue;
    }
    String loggerArmedSecond = null;
    public String getLoggerArmedSecond() {
        appendToLog("TagAxzonOpus.getLoggerArmedSecond 1");
        if (loggerArmedSecond == null) {
            appendToLog("TagAxzonOpus.getLoggerArmedSecond 2");
            int iOffset = 0x11;
            if (stringsTid == null || stringsTid.length < (iOffset + 2) || stringsTid[iOffset] == null || stringsTid[iOffset+1] == null) {
                setTidBankData8ReadStart();
                return null;
            } else {
                appendToLog("TagAxzonOpus.getLoggerArmedSecond 3 with string as " + stringsTid[iOffset] + ", " + stringsTid[iOffset+1]);
                int iValueM = Integer.valueOf(stringsTid[iOffset], 16);
                int iValueL = Integer.valueOf(stringsTid[iOffset+1], 16);
                appendToLog("TagAxzonOpus.getLoggerArmedSecond 3 with iValue as " + iValueM + ", " + iValueL);

                long epoch = ((iValueM & 0xFFFF) << 16) + (iValueL & 0xFFFF);
                loggerArmedSecond = utility.getStringEpochSecond(epoch);
            }
        }
        return loggerArmedSecond;
    }
    public void setLoggerArmedSecond() {
        appendToLog("TagAxzonOpus.setLoggerArmedSecond starts");
        int iOffset = 0x11;
        long epoch = Instant.now().toEpochMilli() / 1000;
        String string = String.format("%X", epoch);
        appendToLog("TagAxzonOpus.setLoggerArmedSecond: epoch = " + epoch + ", string = " + string);
        setBankDataStart(selectData, 2, iOffset , 2, string);
        loggerArmedSecond = null;
    }
    String configAddress = null;
    public String getConfigAddress() {
        appendToLog("TagAxzonOpus.getConfigAddress 1");
        if (configAddress == null) {
            appendToLog("TagAxzonOpus.getConfigAddress 2");
            int iOffset = 0x1A;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setBankDataStart(selectData, 2, iOffset, 1, null);
                return null;
            }
            String string = stringsTid[iOffset];
            appendToLog("TagAxzonOpus.getConfigAddress 3 with string = " + string);
            int iValue = Integer.valueOf(string, 16);
            iValue &= 0x3FF;
            configAddress = String.format("%03X", iValue);
            appendToLog("TagAxzonOpus.getConfigAddress 3 with configAddress as " + configAddress);
        }
        return configAddress;
    }
    public enum SampleNumberToLogTypes {
        SIZE_512, SIZE_1024, SIZE_1536, SIZE_2048, SIZE_2560, SIZE_3072, SIZE_3584, SIZE_4096
    }
    SampleNumberToLogTypes sampleNumberToLogType = null;
    public SampleNumberToLogTypes getSampleNumberToLog() {
        appendToLog("TagAxzonOpus.getSampleNumberToLogType 1");
        if (sampleNumberToLogType == null) {
            appendToLog("TagAxzonOpus.getSampleNumberToLogType 2");
            int iOffset = 0x1A;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setBankDataStart(selectData, 2, iOffset, 1, null);
                return null;
            }
            appendToLog("TagAxzonOpus.getSampleNumberToLogType 3 with stringsTid[" + iOffset + "] = " + stringsTid[iOffset]);
            sampleNumberToLogType = null;
            int iValue = Integer.parseInt(stringsTid[iOffset], 16) >> 10;
            appendToLog("TagAxzonOpus.getSampleNumberToLogType 3 with iValue = "  + iValue);
            sampleNumberToLogType = SampleNumberToLogTypes.values()[iValue];
        }
        return sampleNumberToLogType;
    }
    String nextLogAddress = null;
    public String getNextLogAddress() {
        appendToLog("TagAxzonOpus.getNextLogAddress 1");
        if (true || nextLogAddress == null) {
            appendToLog("TagAxzonOpus.getNextLogAddress 2");
            int iOffset = 0x1B;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return null;
            }
            appendToLog("TagAxzonOpus.getNextLogAddress 3 with string as " + stringsTid[iOffset]);
            nextLogAddress = stringsTid[iOffset];
            if (nextLogAddress.substring(0,1).compareTo("0") != 0) nextLogAddress += "(full)";
            appendToLog("TagAxzonOpus.getNextLogAddress 3 with nextLogAddress as " + nextLogAddress);

            stringsTid[iOffset] = null;
        }
        return nextLogAddress;
    }
    public boolean setNextLogAddress(int iValue) {
        appendToLog("TagAxzonOpus.setNextLogAddress with input = " + iValue);
        int iOffset = 0x1B;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            appendToLog("TagAxzonOpus.setNextLogAddress with StringsTid[" + iOffset + "]" + (stringsTid == null ? " = null" : (".length = " + stringsTid.length)));
            String[] strings = new String[iOffset+1];
            for (int i = 0; i < stringsTid.length; i++) strings[i] = stringsTid[i];
            stringsTid = strings;
        } else {
            String string = String.format("%06X", iValue & 0xFFFF), stringTid = stringsTid[iOffset];
            appendToLog("TagAxzonOpus.setNextLogAddress with string = " + string + ", stringTid = " + stringTid);
            if (stringTid.matches(string)) {
                appendToLog("TagAxzonOpus.setNextLogAddress with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        String string = String.format("%04X", iValue & 0xFFF);
        appendToLog("TagAxzonOpus.setNextLogAddress with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        return false;
    }
    int minBattery4Logging = iNO_SUCH_SETTING;
    public int getMinBattery4Logging() {
        appendToLog("TagAxzonOpus.getMinBattery4Logging 1");
        if (minBattery4Logging == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getMinBattery4Logging 2");
            int iOffset = 0x1c;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getMinBattery4Logging 3 with string as " + stringsTid[0x10]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getMinBattery4Logging 3 with iValue as " + iValue);
                minBattery4Logging = iValue;
                appendToLog("TagAxzonOpus.getMinBattery4Logging 3 with iValue as " + iValue);
            }
        }
        return minBattery4Logging;
    }
    public boolean setMinBattery4Logging(int iValue) {
        appendToLog("TagAxzonOpus.setMinBattery4Logging with input = " + iValue);
        int iOffset = 0x1c;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            appendToLog("TagAxzonOpus.setMinBattery4Logging with StringsTid[" + iOffset + "]" + (stringsTid == null ? " = null" : (".length = " + stringsTid.length)));
            String[] strings = new String[iOffset+1];
            for (int i = 0; i < stringsTid.length; i++) strings[i] = stringsTid[i];
            stringsTid = strings;
        } else {
            String string = String.format("%06X", iValue & 0xFFFF), stringTid = stringsTid[iOffset];
            appendToLog("TagAxzonOpus.setMinBattery4Logging with string = " + string + ", stringTid = " + stringTid);
            if (stringTid.matches(string)) {
                appendToLog("TagAxzonOpus.setMinBattery4Logging with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        String string = String.format("%04X", iValue & 0xFFF);
        appendToLog("TagAxzonOpus.setMinBattery4Logging with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        minBattery4Logging = iValue;
        return false;
    }
    int minBattery4Arming = iNO_SUCH_SETTING;
    public int getMinBattery4Arming() {
        appendToLog("TagAxzonOpus.getMinBattery4Arming 1");
        if (minBattery4Arming == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getMinBattery4Arming 2");
            int iOffset = 0x1d;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getMinBattery4Arming 3 with string as " + stringsTid[0x10]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getMinBattery4Arming 3 with iValue as " + iValue);
                minBattery4Arming = iValue;
                appendToLog("TagAxzonOpus.getMinBattery4Arming 3 with iValue as " + iValue);
            }
        }
        return minBattery4Arming;
    }
    public boolean setMinBattery4Arming(int iValue) {
        appendToLog("TagAxzonOpus.setMinBattery4Arming with input = " + iValue);
        int iOffset = 0x1d;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            appendToLog("TagAxzonOpus.setMinBattery4Arming with StringsTid[" + iOffset + "]" + (stringsTid == null ? " = null" : (".length = " + stringsTid.length)));
            String[] strings = new String[iOffset+1];
            for (int i = 0; i < stringsTid.length; i++) strings[i] = stringsTid[i];
            stringsTid = strings;
        } else {
            String string = String.format("%06X", iValue & 0xFFFF), stringTid = stringsTid[iOffset];
            appendToLog("TagAxzonOpus.setMinBattery4Arming with string = " + string + ", stringTid = " + stringTid);
            if (stringTid.matches(string)) {
                appendToLog("TagAxzonOpus.setMinBattery4Arming with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        String string = String.format("%04X", iValue & 0xFFF);
        appendToLog("TagAxzonOpus.setMinBattery4Arming with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        minBattery4Arming = iValue;
        return false;
    }
    public enum DisableEnableTypes {
        DISABLE, ENABLE
    }
    DisableEnableTypes fingerSpotLedType = null;
    public DisableEnableTypes getFingerSpotLedType() {
        appendToLog("TagAxzonOpus.getFingerSpotLedType 1");
        if (fingerSpotLedType == null) {
            appendToLog("TagAxzonOpus.getFingerSpotLedType 2");
            int iOffset = 0x1E;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return null;
            }
            appendToLog("TagAxzonOpus.getFingerSpotLedType 3 with stringsTid[" + iOffset + "] = " + stringsTid[iOffset]);
            fingerSpotLedType = null;
            int iValue = Integer.parseInt(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getFingerSpotLedType 3 with iValue = " + iValue);
            iValue &= 0x08;
            if (iValue == 0) fingerSpotLedType = DisableEnableTypes.DISABLE;
            else fingerSpotLedType = DisableEnableTypes.ENABLE;
            appendToLog("TagAxzonOpus.getFingerSpotLedType 3 with fingerSpotLedType = " + fingerSpotLedType.toString());
        }
        return fingerSpotLedType;
    }
    DisableEnableTypes ledModeType = null;
    public DisableEnableTypes getLedModeType() {
        appendToLog("TagAxzonOpus.getLedModeType 1");
        if (ledModeType == null) {
            appendToLog("TagAxzonOpus.getLedModeType 2");
            int iOffset = 0x1E;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return null;
            }
            appendToLog("TagAxzonOpus.getLedModeType 3 with stringsTid[" + iOffset + "] = " + stringsTid[iOffset]);
            ledModeType = null;
            int iValue = Integer.parseInt(stringsTid[iOffset], 16);
            appendToLog("TagAxzonOpus.getLedModeType 3 with iValue = " + iValue);
            iValue &= 0x800;
            if (iValue == 0) ledModeType = DisableEnableTypes.DISABLE;
            else ledModeType = DisableEnableTypes.ENABLE;
            appendToLog("TagAxzonOpus.getLedModeType 3 with ledModeType = " + ledModeType.toString());
        }
        return ledModeType;
    }
    public boolean setLedModeType(DisableEnableTypes ledModeType) {
        appendToLog("TagAxzonOpus.setLedModeType with ledModeType = " + ledModeType.toString());
        int iOffset = 0x1E;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            setTidBankData1AReadStart();
            return false;
        } else {
            if (ledModeType == this.ledModeType) {
                appendToLog("TagAxzonOpus.setLedModeType with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        int iValue = Integer.parseInt(stringsTid[iOffset], 16);
        iValue &= ~0x800;
        if (ledModeType == DisableEnableTypes.ENABLE) iValue |= 0x800;
        String string = String.format("%04X", iValue & 0xFFF);
        appendToLog("TagAxzonOpus.setLedModeType with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        this.ledModeType = ledModeType;
        return false;
    }
    int ledOn = iNO_SUCH_SETTING;
    public int getLedOn() {
        appendToLog("TagAxzonOpus.getLedOn 1");
        if (ledOn == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getLedOn 2");
            int iOffset = 0x1E;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getLedOn 3 with string as " + stringsTid[iOffset]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getLedOn 3 with iValue as " + iValue);
                iValue &= 0xF;
                ledOn = iValue;
                appendToLog("TagAxzonOpus.getLedOn 3 with iValue as " + iValue);
            }
        }
        return ledOn;
    }
    public boolean setLedOn(int iValue) {
        appendToLog("TagAxzonOpus.setLedOn with input = " + iValue);
        int iOffset = 0x1E;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            setTidBankData1AReadStart();
            return false;
        } else {
            if (iValue == ledOn) {
                appendToLog("TagAxzonOpus.setLedOn with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        iValue &= 0x0F;
        int iValue1 = Integer.valueOf(stringsTid[iOffset], 16);
        iValue1 &= ~0x0F;
        iValue1 |= iValue;
        String string = String.format("%04X", iValue1 & 0xFFF);
        appendToLog("TagAxzonOpus.setLedOn with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        ledOn = iValue;
        return false;
    }
    int ledOff = iNO_SUCH_SETTING;
    public int getLedOff() {
        appendToLog("TagAxzonOpus.getLedOff 1");
        if (ledOff == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getLedOff 2");
            int iOffset = 0x1E;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getLedOff 3 with string as " + stringsTid[iOffset]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getLedOff 3 with iValue as " + iValue);
                iValue &= 0xF0;
                ledOff = (iValue >> 4);
                appendToLog("TagAxzonOpus.getLedOff 3 with iValue as " + iValue);
            }
        }
        return ledOff;
    }
    public boolean setLedOff(int iValue) {
        appendToLog("TagAxzonOpus.setLedOn with input = " + iValue);
        int iOffset = 0x1E;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            setTidBankData1AReadStart();
            return false;
        } else {
            if (iValue == ledOn) {
                appendToLog("TagAxzonOpus.setLedOn with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        iValue &= 0x0F;
        int iValue1 = Integer.valueOf(stringsTid[iOffset], 16);
        iValue1 &= ~0xF0;
        iValue1 |= (iValue << 4);
        String string = String.format("%04X", iValue1 & 0xFFF);
        appendToLog("TagAxzonOpus.setLedOn with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        ledOff = iValue;
        return false;
    }
    int bapDuration = iNO_SUCH_SETTING;
    public int getBAPduration() {
        appendToLog("TagAxzonOpus.getBAPduration 1");
        if (bapDuration == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getBAPduration 2");
            int iOffset = 0x1F;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getBAPduration 3 with string as " + stringsTid[iOffset]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getBAPduration 3 with iValue as " + iValue);
                iValue &= 0x1F;
                bapDuration = iValue;
                appendToLog("TagAxzonOpus.getBAPduration 3 with iValue as " + iValue);
            }
        }
        return bapDuration;
    }
    public void setBAPduration(int iValue) {
        appendToLog("TagAxzonOpus.setBapDuration with input iValue = " + iValue);

        if (iValue < 0) iValue = 0;
        else if (iValue > 31) iValue = 31;

        int iOffset = 0x1F;
        int iValue1 = Integer.valueOf(stringsTid[iOffset], 16);
        appendToLog("TagAxzonOpus.setBapDuration 1 with iValue1 as " + String.format("%04X", iValue1));
        iValue1 &= ~0x1F;
        iValue1 |= iValue;
        appendToLog("TagAxzonOpus.setBapDuration 2 with iValue1 as " + String.format("%04X", iValue1));

        String string = String.format("%04X", iValue1);
        appendToLog("TagAxzonOpus.setBapDuration with string = " + string);

        setBankDataStart(selectData, 2, iOffset , 1, string);
        bapDuration = iValue;
    }
    DisableEnableTypes writePermaLockType = null;
    public DisableEnableTypes getWritePermaLockType() {
        appendToLog("TagAxzonOpus.getWritePermaLockType 1");
        if (writePermaLockType == null) {
            appendToLog("TagAxzonOpus.getWritePermaLockType 2");
            int iOffset = 0x1F;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return null;
            } else {
                appendToLog("TagAxzonOpus.getWritePermaLockType 3 with string as " + stringsTid[iOffset]);
                writePermaLockType = null;
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getWritePermaLockType 3 with iValue as " + iValue);
                iValue &= 0x40;
                if (iValue == 0) writePermaLockType = DisableEnableTypes.DISABLE;
                else writePermaLockType = DisableEnableTypes.ENABLE;
                appendToLog("TagAxzonOpus.getWritePermaLockType 3 with writePermaLockType = " + writePermaLockType.toString());
            }
        }
        return writePermaLockType;
    }
    public boolean setWritePermaLockType(DisableEnableTypes writePermaLockType) {
        appendToLog("TagAxzonOpus.setWritePermaLockType with ledModeType = " + writePermaLockType.toString());
        int iOffset = 0x1F;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            setTidBankData1AReadStart();
            return false;
        } else {
            if (writePermaLockType == this.writePermaLockType) {
                appendToLog("TagAxzonOpus.setWritePermaLockType with SAME data with getReadWriteStatus = " + (getReadWriteStatus() == null ? "null" : getReadWriteStatus().toString()));
                return true;
            }
        }
        int iValue = Integer.parseInt(stringsTid[iOffset], 16);
        iValue &= ~0x40;
        if (writePermaLockType == DisableEnableTypes.ENABLE) iValue |= 0x40;
        String string = String.format("%04X", iValue & 0xFFF);
        appendToLog("TagAxzonOpus.setWritePermaLockType with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        this.writePermaLockType = writePermaLockType;
        return false;
    }
    int samplesPerMeasure = iNO_SUCH_SETTING;
    public int getSamplesPerMeasure() {
        appendToLog("TagAxzonOpus.getSamplesPerMeasure 1");
        if (samplesPerMeasure == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getSamplesPerMeasure 2");
            int iOffset = 0x1F;
            if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
                setTidBankData1AReadStart();
                return iNO_SUCH_SETTING;
            } else {
                appendToLog("TagAxzonOpus.getSamplesPerMeasure 3 with string as " + stringsTid[iOffset]);
                int iValue = Integer.valueOf(stringsTid[iOffset], 16);
                appendToLog("TagAxzonOpus.getSamplesPerMeasure 3 with iValue as " + iValue);
                iValue &= 0xF00;
                samplesPerMeasure = (iValue >> 8);
                appendToLog("TagAxzonOpus.getSamplesPerMeasure 3 with iValue as " + iValue);
            }
        }
        return samplesPerMeasure;
    }
    public boolean setSamplesPerMeasure(int iValue) {
        appendToLog("TagAxzonOpus.setSamplesPerMeasure with input = " + iValue);
        int iOffset = 0x1F;
        if (stringsTid == null || stringsTid.length <= iOffset || stringsTid[iOffset] == null) {
            setTidBankData1AReadStart();
            return false;
        } else {
            if (iValue == samplesPerMeasure) {
                appendToLog("TagAxzonOpus.setSamplesPerMeasure with SAME data");
                return true;
            }
        }
        iValue &= 0x0F;
        int iValue1 = Integer.valueOf(stringsTid[iOffset], 16);
        iValue1 &= ~0xF00;
        iValue1 |= (iValue << 8);
        String string = String.format("%04X", iValue1 & 0xFFF);
        appendToLog("TagAxzonOpus.setSamplesPerMeasure with string = " + string);
        setBankDataStart(selectData,2, iOffset, 1, string);
        stringsTid[iOffset] = string;
        samplesPerMeasure = iValue;
        return false;
    }
    String ssdAddress = null;
    public String getSsdAddress() {
        appendToLog("TagAxzonOpus.getSsdAddress 1");
        if (ssdAddress == null) {
            appendToLog("TagAxzonOpus.getSsdAddress 2");
            int iOffset = 0x26;
            if (stringsTid == null || stringsTid.length < (iOffset + 2) || stringsTid[iOffset] == null || stringsTid[iOffset+1] == null) {
                setBankDataStart(selectData, 2, iOffset, 2, null);
                return null;
            }
            String string = stringsTid[iOffset] + stringsTid[iOffset+1];
            appendToLog("TagAxzonOpus.getSsdAddress 3 with string = " + string);
            ssdAddress = string;
            appendToLog("TagAxzonOpus.getSsdAddress 3 with ssdAddress as " + rtcAddress);
        }
        return ssdAddress;
    }
    String rtcAddress = null;
    public String getRtcAddress() {
        appendToLog("TagAxzonOpus.getRtcAddress 1");
        if (rtcAddress == null) {
            appendToLog("TagAxzonOpus.getRtcAddress 2");
            int iOffset = 0x28;
            if (stringsTid == null || stringsTid.length < (iOffset + 2) || stringsTid[iOffset] == null || stringsTid[iOffset+1] == null) {
                setBankDataStart(selectData, 2, iOffset, 2, null);
                return null;
            }
            String string = stringsTid[iOffset] + stringsTid[iOffset+1];
            appendToLog("TagAxzonOpus.getRtcAddress 3 with string = " + string);
            rtcAddress = string;
            appendToLog("TagAxzonOpus.getRtcAddress 3 with rtcAddress as " + rtcAddress);
        }
        return rtcAddress;
    }
    int batteryLevel = iNO_SUCH_SETTING;
    public int getBatteryLevel(boolean request) {
        appendToLog("TagAxzonOpus.getBatteryLevel 1");
        if (batteryLevel == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getBatteryLevel 2");
            int iOffset = 3;
            if (request || stringsUser == null || stringsUser.length <= iOffset || stringsUser[iOffset] == null) {
                selectData.bSelectMeasureBattery = true;
                setBankDataStart(selectData, 3, iOffset, 1, null);
                return iNO_SUCH_SETTING;
            }
            appendToLog("TagAxzonOpus.getBatteryLevel 3 with string as " + stringsUser[iOffset]);
            int iValue = Integer.valueOf(stringsUser[iOffset], 16);
            appendToLog("TagAxzonOpus.getBatteryLevel 3 with iValue = " + iValue);
            iValue >>= 4;
            batteryLevel = iValue;
            appendToLog("TagAxzonOpus.getBatteryLevel 3 with batteryLevel as " + batteryLevel);
            stringsUser[iOffset] = null;
        }
        return batteryLevel;
    }
    public enum LoggerStateTypes {
        SLEEP, STANDBY, STATE2, READY,
        STATE4, BAP, LOGGING, FINISHED
    }
    LoggerStateTypes loggerUserStateType = null;
    public LoggerStateTypes getLoggerUserStateType() {
        appendToLog("TagAxzonOpus.getLoggerStateType 1");
        if (true || loggerUserStateType == null) {
            appendToLog("TagAxzonOpus.getLoggerStateType 2 with stringsUser.length = " + (stringsUser == null ? "null" : stringsUser.length));
            int iOffset = 5;
            if (stringsUser == null || stringsUser.length < (iOffset + 1) || stringsUser[iOffset] == null) {
                setBankDataStart(selectData, 3, iOffset, 1, null);
                return null;
            }
            appendToLog("TagAxzonOpus.getLoggerStateType 3 with stringsUser[" + iOffset + "] = " + stringsUser[iOffset]);
            int iValue = Integer.parseInt(stringsUser[iOffset], 16);
            appendToLog("TagAxzonOpus.getLoggerStateType 3 with iValue = " + iValue);
            iValue &= 0x1F;
            loggerUserStateType = LoggerStateTypes.values()[iValue];
            appendToLog("TagAxzonOpus.getLoggerStateType 3 with loggerStateType = " + loggerUserStateType.toString());
            stringsUser[iOffset] = null;
        }
        if (false && loggerUserStateType == null) {
            appendToLog("TagAxzonOpus.getLoggerStateType 2");
            int iOffset = 1;
            if (stringsEpc == null || stringsEpc.length < (iOffset + 1) || stringsEpc[iOffset] == null) {
                setBankDataStart(selectData, 1, iOffset, 1,  null);
                return null;
            }
            appendToLog("TagAxzonOpus.getLoggerStateType 3 with stringsEpc[1] = " + stringsEpc[iOffset]);
            int iValue = Integer.parseInt(stringsEpc[iOffset], 16);
            appendToLog("TagAxzonOpus.getLoggerStateType 3 with iValue = " + iValue);
            iValue = iValue >> 5;
            iValue &= 0x7;
            loggerUserStateType = LoggerStateTypes.values()[iValue];
            appendToLog("TagAxzonOpus.getLoggerStateType 3 with loggerStateType = " + loggerUserStateType.toString());

            stringsEpc[1] = null;
        }
        return loggerUserStateType;
    }
    int clock = iNO_SUCH_SETTING;
    public int getClock() {
        appendToLog("TagAxzonOpus.getClock 1");
        if (true || clock == iNO_SUCH_SETTING) {
            appendToLog("TagAxzonOpus.getClock 2");
            int iOffset = 6;
            if (stringsUser == null || stringsUser.length < (iOffset + 2) || stringsUser[iOffset] == null || stringsUser[iOffset+1] == null ) {
                setBankDataStart(selectData, 3, iOffset, 2, null);
                return iNO_SUCH_SETTING;
            }
            appendToLog("TagAxzonOpus.getClock 3 with string as " + stringsUser[iOffset+1] + " " + stringsUser[iOffset]);
            String string = stringsUser[iOffset + 1].substring(2,4) + stringsUser[iOffset];
            clock = Integer.valueOf(string, 16);
            stringsUser[iOffset] = null;
        }
        return clock;
    }
    public static class LoggerData {
        public boolean tamper;
        public float temperature;
    }
    public LoggerData[] getLoggerData(int index, int ilen) {
        int iOffset = 0xA0 + index;
        appendToLog("TagAxzonOpus.getLoggerData 1 with index = " + index + ", offset = " + iOffset + ", iLen = " + ilen + ", stringUser is " + (stringsUser == null ? "null" : stringsUser.length));
        if (stringsUser != null && stringsUser.length >= (iOffset + ilen)) { appendToLog("TagAxzonOpus.getLoggerData 1a"); }
        if (stringsUser == null || stringsUser.length < (iOffset + ilen) || stringsUser[iOffset + ilen - 1] == null) {
            setBankDataStart(selectData, 3, iOffset, ilen, null);
            return null;
        }
        appendToLog("TagAxzonOpus.getLoggerData 1b with iOffset = " + iOffset + ", iLen = " + ilen);
        LoggerData[] loggerData = new LoggerData[ilen];
        for (int i = 0; i < ilen; i++) {
            appendToLog("TagAxzonOpus.getLoggerData 2 with stringsUser[" + (iOffset + i) + "] as " + stringsUser[iOffset + i]);
            int iValue = Integer.valueOf(stringsUser[iOffset + i], 16);
            boolean tamper = ((iValue & 0x01) != 0);
            iValue >>= 4;
            if ((iValue & 0x800) != 0) {
                iValue |= 0xFFFFF000;
            }
            float fValue = iValue;
            fValue /= 16;
            appendToLog("TagAxzonOpus.getLoggerData 3 with fValue as " + fValue);

            LoggerData loggerData1 = new LoggerData();
            loggerData1.tamper = tamper;
            loggerData1.temperature = fValue;
            loggerData[i] = loggerData1;
        }
        return loggerData;
    }
    void setTidBankData8ReadStart() {
        setBankDataStart(selectData, 2, 8, 16, null);
    }
    void setTidBankData1AReadStart() {
        setBankDataStart(selectData, 2, 0x1A, 6, null);
    }
    void appendToLog(String string) {
        Log.i(TAG, string);
    }
}
