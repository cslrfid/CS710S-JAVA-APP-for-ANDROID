package com.csl.cslibrary4a;

import android.content.Context;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class Cs710Library4A {
    final boolean DEBUG = false;
    Context context;
    CsReaderConnector csReaderConnector; Utility utility;
    boolean DEBUG_CONNECT, DEBUG_SCAN;
    BluetoothGatt bluetoothGatt;
    public Cs710Library4A(Context context, TextView mLogView) {
        this.context = context;
        utility = new Utility(context, mLogView);
        csReaderConnector = new CsReaderConnector(context, mLogView, utility, false); csReaderConnector.setScanType(0x02);
        bluetoothGatt = csReaderConnector.bluetoothGatt; DEBUG_CONNECT = utility.DEBUG_CONNECT; DEBUG_SCAN = utility.DEBUG_SCAN;

        File path = context.getFilesDir();
        File[] fileArray = path.listFiles();
        boolean deleteFiles = false;
        if (true || DEBUG)
            appendToLog("Number of file in data storage sub-directory = " + fileArray.length);
        boolean bProfileInstalledFound = false;
        for (int i = 0; i < fileArray.length; i++) {
            String fileName = fileArray[i].toString();
            if (true) appendToLog("Stored file (" + i + ") = " + fileName);
            if (fileName.contains("profileInstalled") || fileName.contains("profileinstaller")) {
                bProfileInstalledFound = true;
                appendToLog("Found profileInstalled or profileinstaller file");
            }
            File file = new File(fileName);
            if (deleteFiles) file.delete();
        }
        if (!bProfileInstalledFound) {
            for (int i = 0; i < fileArray.length; i++) {
                String fileName = fileArray[i].toString();
                File file = new File(fileName);
                file.delete();
                appendToLog("Deleted " + fileName);
            }
        }
    }
    public String getlibraryVersion() {
        String stringVersion = BuildConfig.VERSION_NAME;
        String stringSubVersion = csReaderConnector.getlibraryVersion();
        return utility.StringVersionHeader +  stringVersion + (stringSubVersion.length() == 0 ? "" : ("-" + stringSubVersion));
    }
    public String checkVersion() {
        return csReaderConnector.checkVersion();
    }

    //============ utility ============
    public String byteArrayToString(byte[] packet) {
        return utility.byteArrayToString(packet);
    }
    public void appendToLog(String s) {
        utility.appendToLog(s);
    }
    public void appendToLogView(String s) {
        utility.appendToLogView(s);
    }
    public float decodeCtesiusTemperature(String strActData, String strCalData) {
        return utility.decodeCtesiusTemperature(strActData, strCalData);
    }
    public float decodeMicronTemperature(int iTag35, String strActData, String strCalData) {
        return utility.decodeMicronTemperature(iTag35, strActData, strCalData);
    }
    public float decodeAsygnTemperature(String string) {
        return utility.decodeAsygnTemperature(string);
    }
    public String getUpcSerial(String strEpc) {
        return utility.getUpcSerial(strEpc);
    }
    public String getUpcSerialDetail(String strUpcSerial) {
        return utility.getUpcSerialDetail(strUpcSerial);
    }
    public String getEpc4upcSerial(int iEpcClass, String filter, String companyPrefix, String itemReference, String serialNumber) {
        return utility.getEpc4upcSerial(iEpcClass, filter, companyPrefix, itemReference, serialNumber);
    }
    public boolean checkHostProcessorVersion(String version, int majorVersion, int minorVersion, int buildVersion) {
        return utility.checkHostProcessorVersion(version, majorVersion, minorVersion, buildVersion);
    }
    public String getFileName4Uri(Uri uri) {
        return utility.getFileName4Uri(uri);
    }

    //============ android bluetooth ============
    public boolean isBleScanning() {
        return bluetoothGatt.isScanning();
    }
    public boolean scanLeDevice(boolean enable) {
        return csReaderConnector.scanLeDevice(enable);
    }
    public BluetoothGatt.CsScanData getNewDeviceScanned() {
        BluetoothGatt.CsScanData csScanData0 = csReaderConnector.getNewDeviceScanned();
        BluetoothGatt.CsScanData csScanData = null;
        if (csScanData0 != null) csScanData = new BluetoothGatt.CsScanData(csScanData0.device, csScanData0.name, csScanData0.address, csScanData0.rssi, csScanData0.scanRecord, csScanData0.decoded_scanRecord, csScanData0.serviceUUID2p2, csScanData0.hasServicePower);
        return csScanData;
    }
    public String getReaderAddress() {
        if (bluetoothGatt.getReaderDeviceConnected() == null) return null;
        return bluetoothGatt.getReaderDeviceConnected().getAddress();
    }
    public String getReaderName() {
        if (bluetoothGatt.getReaderDeviceConnected() == null) return null;
        return bluetoothGatt.getReaderDeviceConnected().getName();
    }
    public boolean isReaderConnected() {
        return csReaderConnector.isReaderConnected();
    }
    public void connect(ReaderDevice readerDevice) {
        ReaderDevice readerDevice0 = null;
        if (readerDevice != null) readerDevice0 = new ReaderDevice(readerDevice.getName(), readerDevice.getAddress(), readerDevice.isConnected(), readerDevice.getServiceUUID2p1());
        csReaderConnector.connect2(readerDevice0);
    }
    public void disconnect(boolean tempDisconnect) {
        csReaderConnector.disconnect(tempDisconnect);
    }
    public boolean forceBTdisconnect() {
        return csReaderConnector.bluetoothConnector.forceBTdisconnect();
    }
    public int getRssi() {
        return bluetoothGatt.getRssi();
    }
    public long getStreamInRate() {
        return csReaderConnector.getStreamInRate();
    }
    public int get98XX() {
        return 2;
    }

    //============ Rfid ============
    //============ Rfid ============
    //============ Rfid ============
    public String getAuthMatchData() {
        return csReaderConnector.rfidReader.getAuthMatchData();
    }
    public boolean setAuthMatchData(String mask) {
        return csReaderConnector.rfidReader.setAuthMatchData(mask);
    }
    public int getStartQValue() {
        return csReaderConnector.rfidReader.getStartQValue();
    }
    public int getMaxQValue() {
        return csReaderConnector.rfidReader.getMaxQValue();
    }
    public int getMinQValue() {
        return csReaderConnector.rfidReader.getMinQValue();
    }
    public boolean setDynamicQParms(int startQValue, int minQValue, int maxQValue, int retryCount) {
        return csReaderConnector.rfidReader.setDynamicQParms(startQValue, minQValue, maxQValue, retryCount);
    }
    public int getFixedQValue() {
        return csReaderConnector.rfidReader.getFixedQValue();
    }
    public int getFixedRetryCount() {
        return csReaderConnector.rfidReader.getFixedRetryCount();
    }
    public boolean getRepeatUnitNoTags() {
        return csReaderConnector.rfidReader.getRepeatUnitNoTags();
    }
    public boolean setFixedQParms(int qValue, int retryCount, boolean repeatUnitNoTags) {
        return csReaderConnector.rfidReader.setFixedQParms(qValue, retryCount, repeatUnitNoTags);
    }
    public boolean getChannelHoppingDefault() {
        return csReaderConnector.rfidReader.getChannelHoppingDefault();
    }
    public boolean getRfidOnStatus() {
        return csReaderConnector.rfidReader.getRfidOnStatus();
    }
    public int getRfidReplyResult() {
        return csReaderConnector.rfidReader.getReplyResult();
    }
    public boolean isRfidFailure() {
        //appendToLog("BtDataOut: isRfidFailure rfidReader is " + (csReaderConnector.rfidReader == null ? "null" : csReaderConnector.rfidReader.isRfidFailure()));
        if (csReaderConnector.rfidReader == null) return false;
        return csReaderConnector.rfidReader.isRfidFailure();
    }
    public void sendRfidImage(byte[] image_subpart_data, int image_total_subpart, int image_subpart) {
        csReaderConnector.rfidReader.rfidConnector.sendImage(image_subpart_data, image_total_subpart, image_subpart);
    }
    public void setReaderDefault() {
        csReaderConnector.setReaderDefault();
    }
    public String getMacVer() {
        return csReaderConnector.rfidReader.getMacVer();
    }
    public String getRadioSerial() {
        return csReaderConnector.rfidReader.getRadioSerial();
    }
    public String getRadioBoardVersion() {
        return csReaderConnector.rfidReader.getRadioBoardVersion();
    }
    public int getPortNumber() {
        if (csReaderConnector.bluetoothConnector.getCsModel() == 203) return 2;
        else return 1;
    }
    public int getAntennaSelect() {
        return csReaderConnector.rfidReader.getAntennaSelect();
    }
    public boolean setAntennaSelect(int number) {
        return csReaderConnector.rfidReader.setAntennaSelect(number);
    }
    public int getAntennaEnable() {
        return csReaderConnector.rfidReader.getAntennaEnable();
    }
    public boolean setAntennaEnable(boolean enable) {
        return csReaderConnector.rfidReader.setAntennaEnable(enable);
    }
    public long getAntennaDwell() {
        return csReaderConnector.rfidReader.getAntennaDwell();
    }
    public boolean setAntennaDwell(long antennaDwell) {
        return csReaderConnector.rfidReader.setAntennaDwell(antennaDwell);
    }
    public long getPwrlevel() {
        return csReaderConnector.rfidReader.getPwrlevel();
    }
    public long getPowerLevelMax() {
        return csReaderConnector.rfidReader.getPowerLevelMax();
    }
    public boolean setPowerLevel(long pwrlevel) {
        return csReaderConnector.rfidReader.setPowerLevel(pwrlevel);
    }
    public int getPowerBoost() {
        return csReaderConnector.rfidReader.getPowerBoost();
    }
    public boolean setPowerBoost(boolean powerBoost) {
        return csReaderConnector.rfidReader.setPowerBoost(powerBoost);
    }
    public int getQueryTarget() {
        return csReaderConnector.rfidReader.getQueryTarget();
    }
    public int getQuerySession() {
        return csReaderConnector.rfidReader.getQuerySession();
    }
    public int getQuerySelect() {
        return csReaderConnector.rfidReader.getQuerySelect();
    }
    public boolean setTagGroup(int sL, int session, int target1) {
        return csReaderConnector.rfidReader.setTagGroup(sL, session, target1);
    }
    public int getTagFocus() {
        return csReaderConnector.rfidReader.getTagFocus();
    }
    public boolean setTagFocus(boolean tagFocusNew) {
        return csReaderConnector.rfidReader.setTagFocus(tagFocusNew);
    }
    public int getFastId() {
        return csReaderConnector.rfidReader.getFastId();
    }
    public boolean setFastId(boolean fastIdNew) {
        appendToLog("bFastId: setFastId[" + fastIdNew);
        return csReaderConnector.rfidReader.setFastId(fastIdNew);
    }
    public int getInvAlgo() {
        return csReaderConnector.rfidReader.getInvAlgo();
    }
    public boolean setInvAlgo(boolean dynamicAlgo) {
        return csReaderConnector.rfidReader.setInvAlgo(dynamicAlgo);
    }
    public List<String> getProfileList() {
        return csReaderConnector.rfidReader.getProfileList();
    }
    public int getCurrentProfile() {
        return csReaderConnector.rfidReader.getCurrentProfile();
    }
    public boolean setBasicCurrentLinkProfile() {
        return csReaderConnector.rfidReader.setBasicCurrentLinkProfile();
    }
    public boolean setCurrentLinkProfile(int profile) {
        return csReaderConnector.rfidReader.setCurrentLinkProfile(profile);
    }
    public void resetEnvironmentalRSSI() {
        csReaderConnector.rfidReader.resetEnvironmentalRSSI();
    }
    public String getEnvironmentalRSSI() {
        return csReaderConnector.rfidReader.getEnvironmentalRSSI();
    }
    public int getHighCompression() {
        return csReaderConnector.rfidReader.getHighCompression();
    }
    public int getRflnaGain() {
        return csReaderConnector.rfidReader.getRflnaGain();
    }
    public int getIflnaGain() {
        return csReaderConnector.rfidReader.getIflnaGain();
    }
    public int getAgcGain() {
        return csReaderConnector.rfidReader.getAgcGain();
    }
    public int getRxGain() {
        return csReaderConnector.rfidReader.getRxGain();
    }
    public boolean setRxGain(int highCompression, int rflnagain, int iflnagain, int agcgain) {
        return csReaderConnector.rfidReader.setRxGain(highCompression, rflnagain, iflnagain, agcgain);
    }
    public boolean setRxGain(int rxGain) {
        return csReaderConnector.rfidReader.setRxGain(rxGain);
    }
    public int FreqChnCnt() {
        return csReaderConnector.rfidReader.FreqChnCnt(csReaderConnector.rfidReader.regionCode);
    }
    public double getLogicalChannel2PhysicalFreq(int channel) {
        return csReaderConnector.rfidReader.getLogicalChannel2PhysicalFreq(channel);
    }
    public byte getTagDelay() {
        return csReaderConnector.rfidReader.getTagDelay();
    }
    public boolean setTagDelay(byte tagDelay) {
        return csReaderConnector.rfidReader.setTagDelay(tagDelay);
    }
    public int getIntraPkDelay() {
        return csReaderConnector.rfidReader.getIntraPkDelay();
    }
    public boolean setIntraPkDelay(byte intraPkDelay) {
        return csReaderConnector.rfidReader.setIntraPkDelay(intraPkDelay);
    }
    public int getDupDelay() {
        return csReaderConnector.rfidReader.getDupDelay();
    }
    public boolean setDupDelay(byte dupElim) {
        return csReaderConnector.rfidReader.setDupDelay(dupElim);
    }
    public long getCycleDelay() {
        return csReaderConnector.rfidReader.getCycleDelay();
    }
    public boolean setCycleDelay(long cycleDelay) {
        return csReaderConnector.rfidReader.setCycleDelay(cycleDelay);
    }
    public void getAuthenticateReplyLength() {
        csReaderConnector.rfidReader.getAuthenticateReplyLength();
    }
    public boolean setTamConfiguration(boolean header, String matchData) {
        return csReaderConnector.rfidReader.setTamConfiguration(header, matchData);
    }
    public boolean setTam1Configuration(int keyId, String matchData) {
        return csReaderConnector.rfidReader.setTam1Configuration(keyId, matchData);
    }
    public boolean setTam2Configuration(int keyId, String matchData, int profile, int offset, int blockId, int protMode) {
        return csReaderConnector.rfidReader.setTam2Configuration(keyId, matchData, profile, offset, blockId, protMode);
    }
    public int getUntraceableEpcLength() {
        return csReaderConnector.rfidReader.getUntraceableEpcLength();
    }
    public boolean setUntraceable(boolean bHideEpc, int ishowEpcSize, int iHideTid, boolean bHideUser, boolean bHideRange) {
        return csReaderConnector.rfidReader.setUntraceable(bHideEpc, ishowEpcSize, iHideTid, bHideUser, bHideRange);
    }
    public boolean setUntraceable(int range, boolean user, int tid, int epcLength, boolean epc, boolean uxpc) {
        return csReaderConnector.rfidReader.setUntraceable(range, user, tid, epcLength, epc, uxpc);
    }
    public boolean setAuthenticateConfiguration() {
        return csReaderConnector.rfidReader.setAuthenticateConfiguration();
    }
    public int getRetryCount() {
        return csReaderConnector.rfidReader.getRetryCount();
    }
    public boolean setRetryCount(int retryCount) {
        return csReaderConnector.rfidReader.setRetryCount(retryCount);
    }
    public int getInvSelectIndex() {
        return csReaderConnector.rfidReader.getInvSelectIndex();
    }
    public boolean getSelectEnable() {
        return csReaderConnector.rfidReader.getSelectEnable();
    }
    public int getSelectTarget() {
        return csReaderConnector.rfidReader.getSelectTarget();
    }
    public int getSelectAction() {
        return csReaderConnector.rfidReader.getSelectAction();
    }
    public int getSelectMaskBank() {
        return csReaderConnector.rfidReader.getSelectMaskBank();
    }
    public int getSelectMaskOffset() {
        return csReaderConnector.rfidReader.getSelectMaskOffset();
    }
    public String getSelectMaskData() {
        return csReaderConnector.rfidReader.getSelectMaskData();
    }
    public boolean setInvSelectIndex(int invSelect) {
        return csReaderConnector.rfidReader.setInvSelectIndex(invSelect);
    }
    public boolean setSelectCriteriaDisable(int index) {
        return csReaderConnector.rfidReader.setSelectCriteriaDisable(index);
    }
    public boolean setSelectCriteria(int index, boolean enable, int target, int action, int bank, int offset, String mask, boolean maskbit) {
        return csReaderConnector.rfidReader.setSelectCriteria(index, enable, target, action, bank, offset, mask, maskbit);
    }
    public boolean setSelectCriteria(int index, boolean enable, int target, int action, int delay, int bank, int offset, String mask) {
        return csReaderConnector.rfidReader.setSelectCriteria(index, enable, target, action, delay, bank, offset, mask);
    }
    public boolean getRssiFilterEnable() {
        return csReaderConnector.rfidReader.getRssiFilterEnable();
    }
    public int getRssiFilterType() {
        return csReaderConnector.rfidReader.getRssiFilterType();
    }
    public int getRssiFilterOption() {
        return csReaderConnector.rfidReader.getRssiFilterOption();
    }
    public boolean setRssiFilterConfig(boolean enable, int rssiFilterType, int rssiFilterOption) {
        return csReaderConnector.rfidReader.setRssiFilterConfig(enable, rssiFilterType, rssiFilterOption);
    }
    public double getRssiFilterThreshold1() {
        return csReaderConnector.rfidReader.getRssiFilterThreshold1();
    }
    public double getRssiFilterThreshold2() {
        return csReaderConnector.rfidReader.getRssiFilterThreshold2();
    }
    public boolean setRssiFilterThreshold(double rssiFilterThreshold1, double rssiFilterThreshold2) {
        return csReaderConnector.rfidReader.setRssiFilterThreshold(rssiFilterThreshold1, rssiFilterThreshold2);
    }
    public long getRssiFilterCount() {
        return csReaderConnector.rfidReader.getRssiFilterCount();
    }
    public boolean setRssiFilterCount(long rssiFilterCount) {
        return csReaderConnector.rfidReader.setRssiFilterCount(rssiFilterCount);
    }
    public boolean getInvMatchEnable() {
        return csReaderConnector.rfidReader.getInvMatchEnable();
    }
    public boolean getInvMatchType() {
        return csReaderConnector.rfidReader.getInvMatchType();
    }
    public int getInvMatchOffset() {
        return csReaderConnector.rfidReader.getInvMatchOffset();
    }
    public String getInvMatchData() {
        return csReaderConnector.rfidReader.getInvMatchData();
    }
    public boolean setPostMatchCriteria(boolean enable, boolean target, int offset, String mask) {
        return csReaderConnector.rfidReader.setPostMatchCriteria(enable, target, offset, mask);
    }
    public int dataToWriteSize() {
        return csReaderConnector.dataToWriteSize();
    }
    public int rfidToWriteSize() {
        return csReaderConnector.rfidToWriteSize();
    }
    public long getTagRate() {
        return csReaderConnector.rfidReader.getTagRate();
    }
    public boolean isInventoring() {
        return csReaderConnector.rfidReader.isInventoring();
    }
    public boolean startOperation(RfidReaderChipData.OperationTypes operationTypes) {
        RfidReaderChipData.OperationTypes operationTypes0 = RfidReaderChipData.OperationTypes.values()[operationTypes.ordinal()];
        return csReaderConnector.rfidReader.startOperation(operationTypes0);
    }
    public boolean abortOperation() {
        return csReaderConnector.rfidReader.abortOperation();
    }
    public void restoreAfterTagSelect() {
        csReaderConnector.restoreAfterTagSelect();
    }
    public boolean setSelectedTagByTID(String strTagId, long pwrlevel) {
        return csReaderConnector.rfidReader.setSelectedTagByTID(strTagId, pwrlevel);
    }
    public boolean setSelectedTag(String strTagId, int selectBank, long pwrlevel) {
        return csReaderConnector.rfidReader.setSelectedTag(strTagId, selectBank, pwrlevel);
    }
    public boolean setSelectedTag4Access(String selectMask, int selectBank, int selectOffset, long pwrlevel, int qValue, int matchRep) {
        return csReaderConnector.rfidReader.setSelectedTag4Access(false, selectMask, selectBank, selectOffset, pwrlevel, qValue, matchRep);
    }
    public boolean setMatchRep(int matchRep) {
        if (utility.DEBUG_INVCFG) appendToLog("Debug_InvCfg: Cs710Library4A.setMatchRep goes to setMatchRep with matchRep = " + matchRep);
        return csReaderConnector.rfidReader.setMatchRep(matchRep);
    }
    public String[] getCountryList() {
        return csReaderConnector.rfidReader.getCountryList();
    }
    public int getCountryNumberInList() {
        return csReaderConnector.rfidReader.countryInList;
    }
    public boolean setCountryInList(int countryInList) {
        return csReaderConnector.rfidReader.setCountryInList(countryInList);
    }
    public boolean getChannelHoppingStatus() {
        return csReaderConnector.rfidReader.getChannelHoppingStatus();
    }
    public boolean setChannelHoppingStatus(boolean channelOrderHopping) {
        return csReaderConnector.rfidReader.setChannelHoppingStatus(channelOrderHopping);
    }
    public String[] getChannelFrequencyList(int iRegionPosition) {
        return csReaderConnector.rfidReader.getChannelFrequencyList(iRegionPosition);
    }
    public int getChannel() {
        return csReaderConnector.settingData.channel;
    }
    public boolean setChannel(int channelSelect) {
        return csReaderConnector.rfidReader.setChannel(channelSelect);
    }
    public int getQ2Population(int iQValue) {
        return csReaderConnector.rfidReader.getQ2Population(iQValue);
    }
    public byte getPopulation2Q(int population) {
        return csReaderConnector.rfidReader.getPopulation2Q(population);
    }
    public int getPopulation() {
        return csReaderConnector.rfidReader.getPopulation();
    }
    public boolean setPopulation(int population) {
        return csReaderConnector.rfidReader.setPopulation(population);
    }
    public byte getQValue() {
        return csReaderConnector.rfidReader.qValueSetting;
    }
    public boolean setQValue(byte byteValue) {
        return csReaderConnector.rfidReader.setQValue(byteValue);
    }
    boolean setQValue1(int iValue) {
        return csReaderConnector.rfidReader.setQValue1(iValue);
    }
    public RfidReaderChipData.Rx000pkgData onRFIDEvent() {
        RfidReaderChipData.Rx000pkgData rx000pkgData0 = csReaderConnector.rfidReader.onRFIDEvent();
        RfidReaderChipData.Rx000pkgData rx000pkgData = null;
        if (rx000pkgData0 != null) {
            rx000pkgData = new RfidReaderChipData.Rx000pkgData();
            appendToLog("Cs710Library4A.onRFIDEvent: rx000pkgData0 is " + (rx000pkgData0 == null ? "null" : "valid"));
            rx000pkgData.getFrom0(rx000pkgData0);
        }
        return rx000pkgData;
    }
    public String getModelNumber() {
        return csReaderConnector.rfidReader.getModelNumber(csReaderConnector.getModelName());
    }
    public boolean setRx000KillPassword(String password) {
        return csReaderConnector.rfidReader.setRx000KillPassword(password);
    }
    public boolean setRx000AccessPassword(String password) {
        return csReaderConnector.rfidReader.setRx000AccessPassword(password);
    }
    public boolean setAccessRetry(boolean accessVerfiy, int accessRetry) {
        return csReaderConnector.rfidReader.setAccessRetry(accessVerfiy, accessRetry);
    }
    public boolean setInvModeCompact(boolean invModeCompact) {
        if (utility.DEBUG_COMPACT) appendToLog("Debug_Compact: Cs710Library4A.setInvModeCompact goes to setInvModeCompact");
        return csReaderConnector.rfidReader.setInvModeCompact(invModeCompact);
    }
    public boolean setAccessLockAction(int accessLockAction, int accessLockMask) {
        return csReaderConnector.rfidReader.setAccessLockAction(accessLockAction, accessLockMask);
    }
    public boolean setAccessBank(int accessBank) {
        return csReaderConnector.rfidReader.setAccessBank(accessBank);
    }
    public boolean setAccessBank(int accessBank, int accessBank2) {
        return csReaderConnector.rfidReader.setAccessBank(accessBank, accessBank2);
    }
    public boolean setAccessOffset(int accessOffset) {
        return csReaderConnector.rfidReader.setAccessOffset(accessOffset);
    }
    public boolean setAccessOffset(int accessOffset, int accessOffset2) {
        return csReaderConnector.rfidReader.setAccessOffset(accessOffset, accessOffset2);
    }
    public boolean setAccessCount(int accessCount) {
        return csReaderConnector.rfidReader.setAccessCount(accessCount);
    }
    public boolean setAccessCount(int accessCount, int accessCount2) {
        return csReaderConnector.rfidReader.setAccessCount(accessCount, accessCount2);
    }
    public boolean setAccessWriteData(String dataInput) {
        return csReaderConnector.rfidReader.setAccessWriteData(dataInput);
    }
    public boolean setResReadNoReply(boolean resReadNoReply) {
        return csReaderConnector.rfidReader.setResReadNoReply(resReadNoReply);
    }
    public boolean setTagRead(int tagRead) {
        return csReaderConnector.rfidReader.setTagRead(tagRead);
    }
    public boolean setInvBrandId(boolean invBrandId) {
        return csReaderConnector.rfidReader.setInvBrandId(invBrandId);
    }
    public boolean setInvAuthenticate(boolean invAuthenticate) {
        return csReaderConnector.rfidReader.setInvAuthenticate(invAuthenticate);
    }
    public boolean sendHostRegRequestHST_CMD(RfidReaderChipData.HostCommands hostCommand) {
        if (true) setInvModeCompact(false);
        RfidReaderChipData.HostCommands hostCommands0 = RfidReaderChipData.HostCommands.values()[hostCommand.ordinal()];
        return csReaderConnector.rfidReader.sendHostRegRequestHST_CMD(hostCommands0);
    }
    public boolean setPwrManagementMode(boolean bLowPowerStandby) {
        if (isReaderConnected() == false) return false;
        return csReaderConnector.rfidReader.setPwrManagementMode(bLowPowerStandby);
    }
    public void macWrite(int address, long value) {
        csReaderConnector.rfidReader.macWrite(address, value);
    }
    public void set_fdCmdCfg(int value) {
        csReaderConnector.rfidReader.set_fdRegAddr(value);
    }
    public void set_fdRegAddr(int addr) {
        csReaderConnector.rfidReader.set_fdRegAddr(addr);
    }
    public void set_fdWrite(int addr, long value) {
        csReaderConnector.rfidReader.set_fdWrite(addr, value);
    }
    public void set_fdPwd(int value) {
        csReaderConnector.rfidReader.set_fdPwd(value);
    }
    public void set_fdBlockAddr4GetTemperature(int addr) {
        csReaderConnector.rfidReader.set_fdBlockAddr4GetTemperature(addr);
    }
    public void set_fdReadMem(int addr, long len) {
        csReaderConnector.rfidReader.set_fdReadMem(addr, len);
    }
    public void set_fdWriteMem(int addr, int len, long value) {
        csReaderConnector.rfidReader.set_fdWriteMem(addr, len, value);
    }
    public void setImpinJExtension(boolean tagFocus, boolean fastId) {
        csReaderConnector.rfidReader.setImpinJExtension(tagFocus, fastId);
    }

    //============ Barcode ============
    public void getBarcodePreSuffix() {
        csReaderConnector.barcodeNewland.getBarcodePreSuffix();
    }
    public void getBarcodeReadingMode() {
        csReaderConnector.barcodeNewland.barcodeSendQueryReadingMode();
    }
    public boolean isBarcodeFailure() {
        if (csReaderConnector.barcodeConnector == null) return false;
        return csReaderConnector.barcodeConnector.barcodeFailure;
    }
    public String getBarcodeDate() {
        return csReaderConnector.barcodeNewland.getBarcodeDate();
    }
    public boolean getBarcodeOnStatus() {
        if (csReaderConnector.barcodeConnector == null) return false;
        return csReaderConnector.barcodeConnector.getOnStatus();
    }
    public boolean setBarcodeOn(boolean on) {
        return csReaderConnector.setBarcodeOn(on);
    }
    public boolean setVibrateOn(int mode) {
        return csReaderConnector.setVibrateOn(mode);
    }
    public boolean getInventoryVibrate() {
        return csReaderConnector.settingData.inventoryVibrate;
    }
    public boolean setInventoryVibrate(boolean inventoryVibrate) {
        boolean DEBUG = false;
        if (DEBUG) appendToLog("this.inventoryVibrate = " + csReaderConnector.settingData.inventoryVibrate + ", inventoryVibrate = " + inventoryVibrate);
        csReaderConnector.settingData.inventoryVibrate = inventoryVibrate;
        if (DEBUG) appendToLog("this.inventoryVibrate = " + csReaderConnector.settingData.inventoryVibrate + ", inventoryVibrate = " + inventoryVibrate);
        return true;
    }
    public int getVibrateTime() {
        return csReaderConnector.settingData.vibrateTimeSetting;
    }
    public boolean setVibrateTime(int vibrateTime) {
        csReaderConnector.settingData.vibrateTimeSetting = vibrateTime;
        return true;
    }
    public int getVibrateWindow() {
        return csReaderConnector.settingData.vibrateWindowSetting;
    }
    public boolean setVibrateWindow(int vibrateWindow) {
        csReaderConnector.settingData.vibrateWindowSetting = vibrateWindow;
        return true;
    }
    public boolean barcodeSendCommandTrigger() {
        return csReaderConnector.barcodeNewland.barcodeSendCommandTrigger();
    }
    public boolean barcodeSendCommandSetPreSuffix() {
        return csReaderConnector.barcodeNewland.barcodeSendCommandSetPreSuffix();
    }
    public boolean barcodeSendCommandResetPreSuffix() {
        return csReaderConnector.barcodeNewland.barcodeSendCommandResetPreSuffix();
    }
    public boolean barcodeSendCommandConinuous() {
        return csReaderConnector.barcodeNewland.barcodeSendCommandConinuous();
    }
    public String getBarcodeVersion() {
        return csReaderConnector.barcodeNewland.getBarcodeVersion();
    }
    public String getBarcodeSerial() {
        return csReaderConnector.barcodeNewland.getBarcodeSerial();
    }
    public boolean barcodeInventory(boolean start) {
        return csReaderConnector.barcodeInventory(start);
    }
    public byte[] onBarcodeEvent() {
        return csReaderConnector.onBarcodeEvent();
    }

    //============ Android General ============
    public void setSameCheck(boolean sameCheck1) {
        if (csReaderConnector.sameCheck == sameCheck1) return;
        if (false) appendToLog("new sameCheck = " + sameCheck1 + ", with old sameCheck = " + csReaderConnector.sameCheck);
        csReaderConnector.sameCheck = sameCheck1; //sameCheck = false;
    }

    public int getBeepCount() {
        return csReaderConnector.settingData.beepCountSetting;
    }
    public boolean setBeepCount(int beepCount) {
        csReaderConnector.settingData.beepCountSetting = beepCount;
        return true;
    }

    public boolean getInventoryBeep() {
        return csReaderConnector.settingData.inventoryBeep;
    }
    public boolean setInventoryBeep(boolean inventoryBeep) {
        csReaderConnector.settingData.inventoryBeep = inventoryBeep;
        return true;
    }

    public boolean getSaveFileEnable() {
        return csReaderConnector.settingData.saveFileEnable;
    }
    public boolean setSaveFileEnable(boolean saveFileEnable) {
        appendToLog("this.saveFileEnable = " + csReaderConnector.settingData.saveFileEnable + ", saveFileEnable = " + saveFileEnable);
        csReaderConnector.settingData.saveFileEnable = saveFileEnable;
        appendToLog("this.saveFileEnable = " + csReaderConnector.settingData.saveFileEnable + ", saveFileEnable = " + saveFileEnable);
        return true;
    }
    public boolean getSaveCloudEnable() {
        return csReaderConnector.settingData.saveCloudEnable;
    }
    public boolean setSaveCloudEnable(boolean saveCloudEnable) {
        csReaderConnector.settingData.saveCloudEnable = saveCloudEnable;
        return true;
    }
    public boolean getSaveNewCloudEnable() {
        return csReaderConnector.settingData.saveNewCloudEnable;
    }
    public boolean setSaveNewCloudEnable(boolean saveNewCloudEnable) {
        csReaderConnector.settingData.saveNewCloudEnable = saveNewCloudEnable;
        return true;
    }
    public boolean getSaveAllCloudEnable() {
        return csReaderConnector.settingData.saveAllCloudEnable;
    }
    public boolean setSaveAllCloudEnable(boolean saveAllCloudEnable) {
        csReaderConnector.settingData.saveAllCloudEnable = saveAllCloudEnable;
        return true;
    }
    public boolean getUserDebugEnable() {
        boolean bValue = csReaderConnector.settingData.userDebugEnable; appendToLog("bValue = " + bValue); return bValue;
    }
    public boolean setUserDebugEnable(boolean userDebugEnable) {
        appendToLog("new userDebug = " + userDebugEnable);
        csReaderConnector.settingData.userDebugEnable = userDebugEnable;
        return true;
    }
    public String getForegroundReader() {
        return csReaderConnector.settingData.strForegroundReader;
    }
    public boolean getForegroundServiceEnable() {
        String string = csReaderConnector.settingData.strForegroundReader;
        return (string.trim().length() == 0 ? false : true);
    }
    public boolean setForegroundServiceEnable(boolean bForegroundService) {
        if (bForegroundService) csReaderConnector.settingData.strForegroundReader = csReaderConnector.bluetoothGatt.getReaderDeviceConnected().getAddress();
        else csReaderConnector.settingData.strForegroundReader = "";
        return true;
    }
    public String getServerLocation() {
        return csReaderConnector.settingData.serverLocation;
    }
    public boolean setServerLocation(String serverLocation) {
        csReaderConnector.settingData.serverLocation = serverLocation;
        return true;
    }
    public int getServerTimeout() {
        return csReaderConnector.settingData.serverTimeout;
    }
    public boolean setServerTimeout(int serverTimeout) {
        csReaderConnector.settingData.serverTimeout = serverTimeout;
        return true;
    }
    public String getServerMqttLocation() {
        return csReaderConnector.settingData.serverMqttLocation;
    }
    public boolean setServerMqttLocation(String serverLocation) {
        csReaderConnector.settingData.serverMqttLocation = serverLocation;
        return true;
    }
    public String getTopicMqtt() {
        return csReaderConnector.settingData.topicMqtt;
    }
    public boolean setTopicMqtt(String topicMqtt) {
        csReaderConnector.settingData.topicMqtt = topicMqtt;
        return true;
    }
    public int getForegroundDupElim() {
        return csReaderConnector.settingData.iForegroundDupElim;
    }
    public boolean setForegroundDupElim(int iForegroundDupElim) {
        csReaderConnector.settingData.iForegroundDupElim = iForegroundDupElim;
        return true;
    }
    public int getInventoryCloudSave() {
        return csReaderConnector.settingData.inventoryCloudSave;
    }
    public boolean setInventoryCloudSave(int inventoryCloudSave) {
        csReaderConnector.settingData.inventoryCloudSave = inventoryCloudSave;
        return true;
    }
    public String getServerImpinjLocation() {
        return csReaderConnector.settingData.serverImpinjLocation;
    }
    public boolean setServerImpinjLocation(String serverImpinjLocation) {
        csReaderConnector.settingData.serverImpinjLocation = serverImpinjLocation;
        return true;
    }
    public String getServerImpinjName() {
        return csReaderConnector.settingData.serverImpinjName;
    }
    public boolean setServerImpinjName(String serverImpinjName) {
        return csReaderConnector.setServerImpinjName(serverImpinjName);
    }
    public String getServerImpinjPassword() {
        return csReaderConnector.settingData.serverImpinjPassword;
    }
    public boolean setServerImpinjPassword(String serverImpinjPassword) {
        return csReaderConnector.setServerImpinjPassword(serverImpinjPassword);
    }
    public String getPartnerReaderName() {
        return csReaderConnector.settingData.partnerReaderName;
    }
    public boolean setPartnerReaderName(String partnerReaderName) {
        return csReaderConnector.setPartnerReaderName(partnerReaderName);
    }
    public int getBatteryDisplaySetting() {
        return csReaderConnector.settingData.batteryDisplaySelect;
    }
    public boolean setBatteryDisplaySetting(int batteryDisplaySelect) {
        return csReaderConnector.settingData.setBatteryDisplaySetting(batteryDisplaySelect);
    }
    public double dBuV_dBm_constant = RfidReader.dBuV_dBm_constant; //106.98;
    public int getRssiDisplaySetting() {
        return csReaderConnector.settingData.rssiDisplaySelect;
    }
    public boolean setRssiDisplaySetting(int rssiDisplaySelect) {
        return csReaderConnector.settingData.setRssiDisplaySetting(rssiDisplaySelect);
    }
    public int getVibrateModeSetting() {
        return csReaderConnector.settingData.vibrateModeSelect;
    }
    public boolean setVibrateModeSetting(int vibrateModeSelect) {
        return csReaderConnector.setVibrateModeSetting(vibrateModeSelect);
    }
    public int getSavingFormatSetting() {
        return csReaderConnector.settingData.savingFormatSelect;
    }
    public boolean setSavingFormatSetting(int savingFormatSelect) {
        return csReaderConnector.setSavingFormatSetting(savingFormatSelect);
    }
    public int getCsvColumnSelectSetting() {
        return csReaderConnector.settingData.csvColumnSelect;
    }
    public boolean setCsvColumnSelectSetting(int csvColumnSelect) {
        return csReaderConnector.setCsvColumnSelectSetting(csvColumnSelect);
    }
    public String getWedgeDeviceName() {
        return csReaderConnector.settingData.wedgeDeviceName;
    }
    public String getWedgeDeviceAddress() {
        return csReaderConnector.settingData.wedgeDeviceAddress;
    }
    public int getWedgeDeviceUUID2p1() {
        return csReaderConnector.settingData.wedgeDeviceUUID2p1;
    }
    public int getWedgePower() {
        return csReaderConnector.settingData.wedgePower;
    }
    public String getWedgePrefix() {
        return csReaderConnector.settingData.wedgePrefix;
    }
    public String getWedgeSuffix() {
        return csReaderConnector.settingData.wedgeSuffix;
    }
    public int getWedgeDelimiter() {
        return csReaderConnector.settingData.wedgeDelimiter;
    }
    public int getWedgeOutput() {
        return csReaderConnector.settingData.wedgeOutput;
    }
    public void setWedgeDeviceName(String wedgeDeviceName) {
        csReaderConnector.settingData.wedgeDeviceName = wedgeDeviceName;
    }
    public void setWedgeDeviceAddress(String wedgeDeviceAddress) {
        csReaderConnector.settingData.wedgeDeviceAddress = wedgeDeviceAddress;
    }
    public void setWedgeDeviceUUID2p1(int wedgeDeviceUUID2p1) {
        csReaderConnector.settingData.wedgeDeviceUUID2p1 = wedgeDeviceUUID2p1;;
    }
    public void setWedgePower(int iPower) {
        csReaderConnector.settingData.wedgePower = iPower;
    }
    public void setWedgePrefix(String string) {
        csReaderConnector.settingData.wedgePrefix = string;
    }
    public void setWedgeSuffix(String string) {
        csReaderConnector.settingData.wedgeSuffix = string;
    }
    public void setWedgeDelimiter(int iValue) {
        csReaderConnector.settingData.wedgeDelimiter = iValue;
    }
    public void setWedgeOutput(int iOutput) {
        csReaderConnector.settingData.wedgeOutput = iOutput;
    }
    public void saveWedgeSetting2File() {
        csReaderConnector.settingData.saveWedgeSetting2File();
    }

    //============ Bluetooth ============
    public boolean isBluetoothICFailure() {
        return csReaderConnector.controllerConnector.isFailure();
    }
    public int getBluetoothICReplyResult() {
        return csReaderConnector.controllerConnector.getReplyResult();
    }
    public String getBluetoothICFirmwareVersion() {
        return csReaderConnector.bluetoothConnector.getBluetoothIcVersion();
    }
    public void sendBluetoothIcImage(byte[] image_subpart_data, int image_total_subpart, int image_subpart) {
        csReaderConnector.controllerConnector.sendImage(true, image_subpart_data, image_total_subpart, image_subpart);
    }
    public String getBluetoothICFirmwareName() {
        return csReaderConnector.bluetoothConnector.getBluetoothIcName();
    }
    public boolean setBluetoothICFirmwareName(String name) {
        return csReaderConnector.bluetoothConnector.setBluetoothIcName(name);
    }

    //============ Controller ============
    public int getControllerReplyResult() {
        return csReaderConnector.controllerConnector.getReplyResult();
    }
    public boolean isControllerFailure() {
        return csReaderConnector.controllerConnector.isFailure();
    }
    public String hostProcessorICGetFirmwareVersion() {
        return csReaderConnector.controllerConnector.getVersion();
    }
    public void sendHostProcessorICImage(byte[] image_subpart_data, int image_total_subpart, int image_subpart) {
        csReaderConnector.controllerConnector.sendImage(false, image_subpart_data, image_total_subpart, image_subpart);
    }
    public String getHostProcessorICSerialNumber() {
        return csReaderConnector.getHostProcessorICSerialNumber();
    }
    public String getHostProcessorICBoardVersion() {
        return csReaderConnector.getHostProcessorICBoardVersion();
    }

    //============ Controller notification ============
    public int getBatteryLevel() {
        return csReaderConnector.getBatteryLevel();
    }
    public boolean setAutoTriggerReporting(byte timeSecond) {
        return csReaderConnector.notificationConnector.setAutoTriggerReporting(timeSecond);
    }
    public boolean getAutoBarStartSTop() {
        return csReaderConnector.notificationConnector.getAutoBarStartStopStatus();
    }

    public boolean batteryLevelRequest() {
        return csReaderConnector.batteryLevelRequest();
    }
    public boolean setAutoBarStartSTop(boolean enable) {
        return csReaderConnector.notificationConnector.setAutoBarStartSTop(enable);
    }
    public boolean getTriggerReporting() {
        return csReaderConnector.settingData.triggerReporting;
    }
    public boolean setTriggerReporting(boolean triggerReporting) {
        return csReaderConnector.notificationConnector.setTriggerReporting(triggerReporting);
    }
    public short getTriggerReportingCount() {
        return csReaderConnector.getTriggerReportingCount();
    }
    public boolean setTriggerReportingCount(short triggerReportingCount) {
        return csReaderConnector.notificationConnector.setTriggerReportingCount(triggerReportingCount);
    }
    public String getBatteryDisplay(boolean voltageDisplay) {
        return csReaderConnector.getBatteryDisplay(voltageDisplay);
    }
    public String isBatteryLow() {
        return csReaderConnector.isBatteryLow();
    }
    public int getBatteryCount() {
        return csReaderConnector.csConnectorData.getVoltageCount();
    }
    public boolean getTriggerButtonStatus() {
        return (csReaderConnector.notificationConnector != null && csReaderConnector.notificationConnector.getTriggerStatus());
    }
    public int getTriggerCount() {
        return csReaderConnector.csConnectorData.getTriggerCount();
    }
    public void setNotificationListener(NotificationConnector.NotificationListener listener) {
        NotificationConnector.NotificationListener listener0 = new NotificationConnector.NotificationListener() {
            @Override
            public void onChange() {
                listener.onChange();
            }
        };
        csReaderConnector.notificationConnector.setNotificationListener(listener0);
    }
    public byte[] onNotificationEvent() {
        return csReaderConnector.onNotificationEvent();
    }

    //============ to be modified ============
    public String getSerialNumber() {
        return csReaderConnector.rfidReader.getSerialNumber();
    }
    public boolean setRfidOn(boolean onStatus) {
        return csReaderConnector.rfidReader.turnOn(onStatus);
    }

    public void saveSetting2File() {
        csReaderConnector.settingData.saveSetting2File(csReaderConnector.bluetoothConnector, getlibraryVersion(), getChannelHoppingStatus());
    }

    public int getCsModel() {
        return csReaderConnector.bluetoothConnector.getCsModel();
    }
    public int getAntennaCycle() {
        return csReaderConnector.rfidReader.getAntennaCycle();
    }
    public boolean setAntennaCycle(int antennaCycle) {
        return csReaderConnector.rfidReader.setAntennaCycle(antennaCycle);
    }
    public boolean setAntennaInvCount(long antennaInvCount) {
        return csReaderConnector.rfidReader.setAntennaInvCount(antennaInvCount);
    }

    public void clearInvalidata() {
        csReaderConnector.clearInvalidata();
    }
    public int getInvalidata() {
        return csReaderConnector.invalidata[0];
    }
    public int getInvalidUpdata() {
        return csReaderConnector.invalidata[1];
    }
    public int getValidata() {
        return csReaderConnector.validata;
    }

    public int setSelectData(RfidReader.TagType tagType, String mDid, boolean bNeedSelectedTagByTID, String stringProtectPassword, int selectFor, int selectHold) {
        RfidReader.TagType tagType1 = RfidReader.TagType.values()[tagType.ordinal()];
        return csReaderConnector.rfidReader.setSelectData4Inventory(tagType1, mDid, bNeedSelectedTagByTID, stringProtectPassword, selectFor, selectHold);
    }
    public String getsTid(RfidReader.TagType tagType) {
        RfidReader.TagType tagType1 = RfidReader.TagType.values()[tagType.ordinal()];
        return csReaderConnector.rfidReader.getsTid(tagType1);
    }
    public RfidReader.TagType getagType(String sTid) {
        RfidReader.TagType tagType0 = csReaderConnector.rfidReader.getagType(sTid);
        RfidReader.TagType tagType = RfidReader.TagType.values()[tagType0.ordinal()];
        return tagType;
    }
    public boolean setOtherInventoryData(RfidReader.TagType tagType, String mDid) {
        RfidReader.TagType tagType1 = RfidReader.TagType.values()[tagType.ordinal()];
        return csReaderConnector.rfidReader.setOtherInventoryData(tagType1, mDid);
    }
    public String[] getEpcClassList() {
        return utility.getEpcClassList();
    }
    public byte[] getProtMode2DecryptedData(byte[] key1, String strAlgo, byte[] dataIn, byte[] iv) {
        return csReaderConnector.rfidReader.getProtMode2DecryptedData(key1, strAlgo, dataIn, iv);
    }
    public AccessTaskCustom getAccessTaskCustom(Button button, boolean invalidRequest, boolean selectOne,
                                                SelectData selectData, RfidReaderChipData.HostCommands hostCommand,
                                                boolean bEnableErrorPopWindow, Runnable updateRunnable,
                                                CustomMediaPlayer playerN, CustomMediaPlayer playerO) {
        AccessTaskCustom accessTaskCustom = new AccessTaskCustom(button, invalidRequest, selectOne,
                selectData, hostCommand,
                bEnableErrorPopWindow, updateRunnable,
                context, csReaderConnector, playerN, playerO);
        return accessTaskCustom;
    }
    public AccessTaskCustom getAccessTaskCustom(Button button, boolean invalidRequest, boolean selectOne,
                                                SelectData selectData, RfidReaderChipData.HostCommands hostCommand,
                                                int qValue, int repeat, boolean resetCount, boolean bSkipClearFilter,
                                                TextView textViewWriteCount, TextView registerRunTime, TextView registerTagGot, TextView registerVoltageLevel, TextView registerYieldView, TextView registerTotalView,
                                                CustomMediaPlayer playerN, CustomMediaPlayer playerO) {
        AccessTaskCustom accessTaskCustom = new AccessTaskCustom(button, invalidRequest, selectOne,
                selectData, hostCommand,
                qValue, repeat, resetCount, bSkipClearFilter,
                textViewWriteCount, registerRunTime, registerTagGot, registerVoltageLevel, registerYieldView, registerTotalView,
                context, csReaderConnector, playerN, playerO);
        return accessTaskCustom;
    }
    public AccessTaskCustom getAccessTaskCustom(Button button, boolean invalidRequest,
                                                SelectData selectData, RfidReaderChipData.HostCommands hostCommand,
                                                CustomMediaPlayer playerN, CustomMediaPlayer playerO) {
        AccessTaskCustom accessTaskCustom = new AccessTaskCustom(button, invalidRequest,
                selectData, hostCommand,
                context, csReaderConnector, playerN, playerO);
        return accessTaskCustom;
    }
    public TagAxzonOpus getTagAxzonOpus(CustomMediaPlayer playerN, CustomMediaPlayer playerO, Button buttonRead, Button buttonWrite) {
        TagAxzonOpus tagAxzonOpus = new TagAxzonOpus(context, csReaderConnector, playerN, playerO, buttonRead, buttonWrite);
        return tagAxzonOpus;
    }
}
