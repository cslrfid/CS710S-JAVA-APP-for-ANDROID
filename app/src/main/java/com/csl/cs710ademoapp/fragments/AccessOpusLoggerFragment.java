package com.csl.cs710ademoapp.fragments;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.csl.cslibrary4a.CustomAsyncTask;
import com.csl.cs710ademoapp.GenericTextWatcher;
import com.csl.cs710ademoapp.MainActivity;
import com.csl.cs710ademoapp.R;
import com.csl.cslibrary4a.ReaderDevice;
import com.csl.cslibrary4a.RfidReaderData;
import com.csl.cslibrary4a.SelectData;
import com.csl.cslibrary4a.TagAxzonOpus;

public class AccessOpusLoggerFragment extends CommonFragment {
    final boolean DEBUG = true;
	EditText editTextRWTagID, editTextAccessRWAccPassword, editTextaccessRWAntennaPower;
    TextView textViewFingerSpotStartup;
    TextView textViewAlarmUpperLimit, textViewAlarmUpperDelayed;
    TextView textViewAlarmLowerLimit, textViewAlarmLowerDelayed;
    TextView textViewFirstTemperatureAlarmAddress;
    TextView textViewFirstTamperAlarmAddress;
    TextView textViewFingerArmedClock;
    TextView textViewLoggingInterval, textViewSamplingRegimePeriod;
    TextView textViewTidAlarm, textViewAlarmLowerDelay, textViewAlarmUpperDelay, textViewLoggingDelayedStart;
    TextView textViewLoggerArmedSecond;
    TextView textViewConfigAddress, textViewLoggingSampleSize;
    TextView textViewNextLogAddress;
    TextView textViewMinBattery4Logging;
    TextView textViewMinBattery4Arming;
    TextView textViewFingerSpotLed, textViewLedMode, textViewLedOn, textViewLedOff;
    TextView textViewBAPduration, textViewWritePermaLock, textViewSamplesPerMeasure;
    TextView textViewSsdAddress;
    TextView textViewRtcAddress;
    TextView textViewBatteryLevel;
    TextView textViewLoggerUserState;
    TextView textViewClock;
    TextView textViewLoggerData;
    TextView textViewPcAlarm, textViewLoggerEpcState, textViewReusableLogger;
    TextView textViewSimpleSensor, textViewXpcAlarm;

    CheckBox checkBoxFingerSpotStartup;
    CheckBox checkBoxAlarmUpperLimit, checkBoxAlarmUpperDelayed;
    CheckBox checkBoxAlarmLowerLimit, checkBoxAlarmLowerDelayed;
    CheckBox checkBoxFirstTemperatureAlarmAddress;
    CheckBox checkBoxFirstTamperAlarmAddress;
    CheckBox checkBoxFingerArmedClock;
    CheckBox checkBoxLoggingInterval, checkBoxSamplingRegimePeriod;
    CheckBox checkBoxTidAlarm, checkBoxAlarmLowerDelay, checkBoxAlarmUpperDelay, checkBoxLoggingDelayedStart;
    CheckBox checkBoxLoggerArmedSecond;
    CheckBox checkBoxConfigAddress, checkBoxLoggingSampleSize;
    CheckBox checkBoxNextLogAddress;
    CheckBox checkBoxMinBattery4Logging;
    CheckBox checkBoxMinBattery4Arming;
    CheckBox checkBoxFingerSpotLed, checkBoxLedMode, checkBoxLedOn, checkBoxLedOff;
    CheckBox checkBoxBAPduration, checkBoxWritePermaLock, checkBoxSamplesPerMeasure;
    CheckBox checkBoxSsdAddress;
    CheckBox checkBoxRtcAddress;
    CheckBox checkBoxBatteryLevel;
    CheckBox checkBoxLoggerUserState;
    CheckBox checkBoxClock;
    CheckBox checkBoxLoggerData;
    CheckBox checkBoxPcAlarm, checkBoxLoggerEpcState, checkBoxReusableLogger;
    CheckBox checkBoxSimpleSensor, checkBoxXpcAlarm;

    Spinner spinnerFingerSpotStartup; CheckBox checkBoxFingerSpotStartEnable, checkBoxTamperDetectEnable, checkBoxTamperDisconnectPolarity, checkBoxSampingRegimeEnable;
    EditText editTextAlarmUpperLimit; TextView textViewAlarmUpperLimitUnit;
    TextView editTextAlarmUpperDelayed;
    EditText editTextAlarmLowerLimit; TextView textViewAlarmLowerLimitUnit;
    TextView editTextAlarmLowerDelayed;
    TextView editTextFirstTemperatureAlarmAddress;
    TextView editTextFirstTamperAlarmAddress;
    TextView editTextFingerArmedClock;
    Spinner spinnerLoggingInterval;
    EditText editTextSamplingRegimePeriod;
    Spinner spinnerTidAlarm; CheckBox checkBoxTidAlarmBattery, checkBoxTidAlarmTamper, checkBoxTidAlarmHighTemperature, checkBoxTidAlarmLowTemperature; EditText editTextAlarmLowerDelay, editTextAlarmUpperDelay, editTextLoggingDelayedStart;
    EditText editTextLoggerArmedSecond;
    TextView editTextConfigAddress;
    Spinner spinnerLoggingSampleSize;
    TextView editTextNextLogAddress;
    EditText editTextMinBattery4Logging;
    EditText editTextMinBattery4Arming;
    Spinner spinnerFingerSpotLed;
    Spinner spinnerLedMode;
    EditText editTextLedOn;
    EditText editTextLedOff;
    EditText editTextBAPduration;
    Spinner spinnerWritePermalock;
    EditText editTextSamplesPerMeasure;
    TextView editTextSsdAddress;
    TextView editTextRtcAddress;
    TextView editTextBatteryLevel;
    Spinner spinnerLoggerUserState;
    TextView editTextClock;
    EditText editTextLoggerDataStart, editTextLoggerDataLength; TextView editTextLoggerData;
    Spinner spinnerPcAlarm; CheckBox checkBoxPcAlarmTamper, checkBoxPcAlarmBattery, checkBoxPcAlarmTemperature, checkBoxPcAlarmBatteryInstalled, checkBoxPcAlarmBatteryConnected;
    Spinner spinnerLoggerEpcState;
    Spinner spinnerReusableLogger;
    Spinner spinnerSimpleSensor;
    Spinner spinnerXpcAlarm; CheckBox checkBoxXpcAlarmArmingBattery, checkBoxXpcAlarmInitialBattery;

    int iUserCode2UnitPosition, iUserCode3UnitPosition;
	private Button buttonRead, buttonWrite, buttonResetLogData, buttonStartLogging;
    enum ReadWriteTypes {
        NULL,
        USERCODE_PC_ALARM, USERCODE_LOGGER_EPC_STATE, USERCODE_REUSABLE_LOGGER,
        USERCODE_XPC_ALARM, USERCODE_SIMPLE_SENSOR,
        USERCODE_FINGERSPOT_STARTUP,
        USERCODE_ALRAM_UPPER_LIMIT, USERCODE_ALARM_UPPER_DELAYED,
        USERCODE_ALRAM_LOWER_LIMIT, USERCODE_ALARM_LOWER_DELAYED,
        USERCODE_FIRST_TEMPERATURE_ALARM_ADDRESS,
        USERCODE_FIRST_TAMPER_ALARM_ADDRESS,
        USERCODE_FINGER_ARMED_CLOCK,
        USERCODE_LOGGING_INTERVAL, USERCODE_SAMPLING_REGIME_PERIOD,
        USERCODE_TID_ALARM, USERCODE_ALRAM_LOWER_DELAY, USERCODE_ALRAM_UPPER_DELAY, USERCODE_LOGGING_DELAYED_START,
        USERCODE_LOGGER_ARMED_SECOND,
        USERCODE_CONFIG_ADDRESS, USERCODE_SAMPLENUMBER_TOLOG,
        USERCODE_NEXT_LOG_ADDRESS,
        USERCODE_MINBATTERY_4LOGGING,
        USERCODE_MINBATTERY_4ARMING,
        USERCODE_FINGERSPOT_LED, USERCODE_LED_MODE, USERCODE_LED_ON, USERCODE_LED_OFF,
        USERCODE_BAP_DURATION, USERCODE_WRITE_PERMALOCK, USERCODE_SAMPLES_PERMEASURE,
        USERCODE_SSD_ADDRESS,
        USERCODE_RTC_ADDRESS,
        USERCODE_BATTERY_LEVEL,
        USERCODE_LOGGER_USER_STATE,
        USERCODE_CLOCK,
        USERCODE_LOGGER_DATA
    }
    ReadWriteTypes readWriteTypes;
    boolean operationRead = false;
    private int modelCode = 0;
    TagAxzonOpus tagAxzonOpus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_access_opus_logger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextRWTagID = (EditText) view.findViewById(R.id.accessXXTagID);
        editTextAccessRWAccPassword = (EditText) view.findViewById(R.id.accessXXAccPasswordValue);
        editTextAccessRWAccPassword.addTextChangedListener(new GenericTextWatcher(editTextAccessRWAccPassword, 8));
        editTextAccessRWAccPassword.setText("00000000");
        editTextaccessRWAntennaPower = (EditText) view.findViewById(R.id.accessXXAntennaPower);
        editTextaccessRWAntennaPower.setText(String.valueOf(300));

        textViewFingerSpotStartup = (TextView) view.findViewById(R.id.accessAxzonTextView11);
        checkBoxFingerSpotStartup = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle11); checkBoxFingerSpotStartup.setText("FingerSpot startup (8)");
        spinnerFingerSpotStartup = (Spinner) view.findViewById(R.id.accessAxzonSpinner11); setupSpinner(spinnerFingerSpotStartup, R.array.tagAxzon_Opus_disable_enable_options); spinnerFingerSpotStartup.setEnabled(false);
        checkBoxFingerSpotStartEnable = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle11m); checkBoxFingerSpotStartEnable.setText("fingerSpot enable"); checkBoxFingerSpotStartEnable.setEnabled(false);
        checkBoxTamperDetectEnable = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle11n); checkBoxTamperDetectEnable.setText("tamper detect enable"); checkBoxTamperDetectEnable.setEnabled(false);
        checkBoxTamperDisconnectPolarity = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle11o); checkBoxTamperDisconnectPolarity.setText("tamper detect polarity"); checkBoxTamperDisconnectPolarity.setEnabled(false);
        checkBoxSampingRegimeEnable = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle11p); checkBoxSampingRegimeEnable.setText("sampling regime enable"); checkBoxSampingRegimeEnable.setEnabled(false);

        textViewAlarmUpperLimit = (TextView) view.findViewById(R.id.accessAxzonTextView2);
        checkBoxAlarmUpperLimit = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle2); checkBoxAlarmUpperLimit.setText("Alarm upper limit (9)");
        editTextAlarmUpperLimit = (EditText) view.findViewById(R.id.accessAxzonEditView2); editTextAlarmUpperLimit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED); editTextAlarmUpperLimit.setEnabled(false);
        textViewAlarmUpperLimitUnit = (TextView) view.findViewById(R.id.accessAxzonTextView222); textViewAlarmUpperLimitUnit.setText("\u00B0C");

        textViewAlarmUpperDelayed = (TextView) view.findViewById(R.id.accessAxzonTextView21);
        checkBoxAlarmUpperDelayed = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle21); checkBoxAlarmUpperDelayed.setText("Alarm upper delayed (9)");
        editTextAlarmUpperDelayed = (TextView) view.findViewById(R.id.accessAxzonEditView21);

        textViewAlarmLowerLimit = (TextView) view.findViewById(R.id.accessAxzonTextView3);
        checkBoxAlarmLowerLimit = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle3); checkBoxAlarmLowerLimit.setText("Alarm lower limit (A)");
        editTextAlarmLowerLimit = (EditText) view.findViewById(R.id.accessAxzonEditView3); editTextAlarmLowerLimit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED); editTextAlarmLowerLimit.setEnabled(false);
        textViewAlarmLowerLimitUnit = (TextView) view.findViewById(R.id.accessAxzonTextView333); textViewAlarmLowerLimitUnit.setText("\u00B0C");

        textViewAlarmLowerDelayed = (TextView) view.findViewById(R.id.accessAxzonTextView20);
        checkBoxAlarmLowerDelayed = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle20); checkBoxAlarmLowerDelayed.setText("Alarm lower delayed (A)");
        editTextAlarmLowerDelayed = (TextView) view.findViewById(R.id.accessAxzonEditView20);

        textViewFirstTemperatureAlarmAddress = (TextView) view.findViewById(R.id.accessAxzonTextView13c);
        checkBoxFirstTemperatureAlarmAddress = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle13c); checkBoxFirstTemperatureAlarmAddress.setText("First Temperature Violation Address (b)");
        editTextFirstTemperatureAlarmAddress = (TextView) view.findViewById(R.id.accessAxzonEditView13c);

        textViewFirstTamperAlarmAddress = (TextView) view.findViewById(R.id.accessAxzonTextView13d);
        checkBoxFirstTamperAlarmAddress = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle13d); checkBoxFirstTamperAlarmAddress.setText("First Tamper Violation Address (c)");
        editTextFirstTamperAlarmAddress = (TextView) view.findViewById(R.id.accessAxzonEditView13d);

        textViewFingerArmedClock = (TextView) view.findViewById(R.id.accessAxzonTextView13b);
        checkBoxFingerArmedClock = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle13b); checkBoxFingerArmedClock.setText("FingerSpot Armed Clock Count (d,E)");
        editTextFingerArmedClock = (TextView) view.findViewById(R.id.accessAxzonEditView13b);

        textViewLoggingInterval = (TextView) view.findViewById(R.id.accessAxzonTextView1);
        checkBoxLoggingInterval = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle1); checkBoxLoggingInterval.setText("Logging interval (F,8)");
        spinnerLoggingInterval = (Spinner) view.findViewById(R.id.accessAxzonSpinner1); setupSpinner(spinnerLoggingInterval, R.array.tagAxzon_Opus_logging_interval_options); spinnerLoggingInterval.setEnabled(false);

        textViewSamplingRegimePeriod = (TextView) view.findViewById(R.id.accessAxzonTextView4x);
        checkBoxSamplingRegimePeriod = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle4x); checkBoxSamplingRegimePeriod.setText("Sampling Regime Period (? F)");
        editTextSamplingRegimePeriod = (EditText) view.findViewById(R.id.accessAxzonEditView4x); editTextSamplingRegimePeriod.setInputType(InputType.TYPE_CLASS_NUMBER); editTextSamplingRegimePeriod.setEnabled(false);

        textViewTidAlarm = (TextView) view.findViewById(R.id.accessAxzonTextView16);
        checkBoxTidAlarm = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle16); checkBoxTidAlarm.setText("TID Alarm(10)");
        spinnerTidAlarm = (Spinner) view.findViewById(R.id.accessAxzonSpinner16); setupSpinner(spinnerTidAlarm, R.array.tagAxzon_Opus_inactive_active_options); spinnerTidAlarm.setEnabled(false);
        checkBoxTidAlarmLowTemperature = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle16a); checkBoxTidAlarmLowTemperature.setText("low temp"); checkBoxTidAlarmLowTemperature.setEnabled(false);
        checkBoxTidAlarmHighTemperature = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle16b); checkBoxTidAlarmHighTemperature.setText("high temp"); checkBoxTidAlarmHighTemperature.setEnabled(false);
        checkBoxTidAlarmTamper = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle16c); checkBoxTidAlarmTamper.setText("tamper"); checkBoxTidAlarmTamper.setEnabled(false);
        checkBoxTidAlarmBattery = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle16d); checkBoxTidAlarmBattery.setText("battery"); checkBoxTidAlarmBattery.setEnabled(false);

        textViewAlarmLowerDelay = (TextView) view.findViewById(R.id.accessAxzonTextView5);
        checkBoxAlarmLowerDelay = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle5); checkBoxAlarmLowerDelay.setText("Alarm lower delay (10)");
        editTextAlarmLowerDelay = (EditText) view.findViewById(R.id.accessAxzonEditView5); editTextAlarmLowerDelay.setInputType(InputType.TYPE_CLASS_NUMBER); editTextAlarmLowerDelay.setEnabled(false);

        textViewAlarmUpperDelay = (TextView) view.findViewById(R.id.accessAxzonTextView4);
        checkBoxAlarmUpperDelay = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle4); checkBoxAlarmUpperDelay.setText("Alarm upper delay (10)");
        editTextAlarmUpperDelay = (EditText) view.findViewById(R.id.accessAxzonEditView4); editTextAlarmUpperDelay.setInputType(InputType.TYPE_CLASS_NUMBER); editTextAlarmUpperDelay.setEnabled(false);

        textViewLoggingDelayedStart = (TextView) view.findViewById(R.id.accessAxzonTextView8);
        checkBoxLoggingDelayedStart = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle8); checkBoxLoggingDelayedStart.setText("Logging Delayed cycles to start (10)");
        editTextLoggingDelayedStart = (EditText) view.findViewById(R.id.accessAxzonEditView8); editTextLoggingDelayedStart.setInputType(InputType.TYPE_CLASS_NUMBER); editTextLoggingDelayedStart.setEnabled(false);

        textViewLoggerArmedSecond = (TextView) view.findViewById(R.id.accessAxzonTextView13a);
        checkBoxLoggerArmedSecond = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle13a); checkBoxLoggerArmedSecond.setText("Logger Armed RTC (11,12)");
        editTextLoggerArmedSecond = (EditText) view.findViewById(R.id.accessAxzonEditView13a);

        textViewConfigAddress = (TextView) view.findViewById(R.id.accessAxzonTextView9c);
        checkBoxConfigAddress = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle9c); checkBoxConfigAddress.setText("? Config Address (1A)");
        editTextConfigAddress = (TextView) view.findViewById(R.id.accessAxzonEditView9c);

        textViewLoggingSampleSize = (TextView) view.findViewById(R.id.accessAxzonTextView80);
        checkBoxLoggingSampleSize = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle80); checkBoxLoggingSampleSize.setText("Logging Sample size (1A)");
        spinnerLoggingSampleSize = (Spinner) view.findViewById(R.id.accessAxzonSpinner80); setupSpinner(spinnerLoggingSampleSize, R.array.tagAxzon_Opus_SampleNumber_ToLog_options); spinnerLoggingSampleSize.setEnabled(false);

        textViewNextLogAddress = (TextView) view.findViewById(R.id.accessAxzonTextView13);
        checkBoxNextLogAddress = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle13); checkBoxNextLogAddress.setText("Next Log Address (1b)");
        editTextNextLogAddress = (TextView) view.findViewById(R.id.accessAxzonEditView13);

        textViewMinBattery4Logging = (TextView) view.findViewById(R.id.accessAxzonTextView1b);
        checkBoxMinBattery4Logging = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle1b); checkBoxMinBattery4Logging.setText("Minimum battery for logging (1c)");
        editTextMinBattery4Logging = (EditText) view.findViewById(R.id.accessAxzonEditView1b); editTextMinBattery4Logging.setInputType(InputType.TYPE_CLASS_NUMBER);

        textViewMinBattery4Arming = (TextView) view.findViewById(R.id.accessAxzonTextView1a);
        checkBoxMinBattery4Arming = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle1a); checkBoxMinBattery4Arming.setText("Minimum battery for arming (1d)");
        editTextMinBattery4Arming = (EditText) view.findViewById(R.id.accessAxzonEditView1a); editTextMinBattery4Arming.setInputType(InputType.TYPE_CLASS_NUMBER);

        textViewFingerSpotLed = (TextView) view.findViewById(R.id.accessAxzonTextView11a);
        checkBoxFingerSpotLed = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle11a); checkBoxFingerSpotLed.setText("FingerSpot LED (1E ?)");
        spinnerFingerSpotLed = (Spinner) view.findViewById(R.id.accessAxzonSpinner11a); setupSpinner(spinnerFingerSpotLed, R.array.tagAxzon_Opus_disable_enable_options); spinnerFingerSpotLed.setEnabled(false);

        textViewLedMode = (TextView) view.findViewById(R.id.accessAxzonTextView92a);
        checkBoxLedMode = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle92a); checkBoxLedMode.setText("Led Mode (1E ?)");
        spinnerLedMode = (Spinner) view.findViewById(R.id.accessAxzonSpinner92a); setupSpinner(spinnerLedMode, R.array.tagAxzon_Opus_continous_onDemand_options); spinnerLedMode.setEnabled(false);

        textViewLedOn = (TextView) view.findViewById(R.id.accessAxzonTextView91a);
        checkBoxLedOn = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle91a); checkBoxLedOn.setText("Led on time (1E ?)");
        editTextLedOn = (EditText) view.findViewById(R.id.accessAxzonEditView91a); editTextLedOn.setInputType(InputType.TYPE_CLASS_NUMBER); editTextLedOn.setEnabled(false);

        textViewLedOff = (TextView) view.findViewById(R.id.accessAxzonTextView90a);
        checkBoxLedOff = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle90a); checkBoxLedOff.setText("Led off time (1E ?)");
        editTextLedOff = (EditText) view.findViewById(R.id.accessAxzonEditView90a); editTextLedOff.setInputType(InputType.TYPE_CLASS_NUMBER); editTextLedOff.setEnabled(false);

        textViewBAPduration = (TextView) view.findViewById(R.id.accessAxzonTextView9a);
        checkBoxBAPduration = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle9a); checkBoxBAPduration.setText("BAP duration (1F)");
        editTextBAPduration = (EditText) view.findViewById(R.id.accessAxzonEditView9a); editTextBAPduration.setInputType(InputType.TYPE_CLASS_NUMBER); editTextBAPduration.setEnabled(false);

        textViewWritePermaLock = (TextView) view.findViewById(R.id.accessAxzonTextView9);
        checkBoxWritePermaLock = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle9); checkBoxWritePermaLock.setText("Write PermaLock (1F ?)");
        spinnerWritePermalock = (Spinner) view.findViewById(R.id.accessAxzonEditView9); setupSpinner(spinnerWritePermalock, R.array.tagAxzon_Opus_disable_enable_options); spinnerWritePermalock.setEnabled(false);

        textViewSamplesPerMeasure = (TextView) view.findViewById(R.id.accessAxzonTextView9b);
        checkBoxSamplesPerMeasure = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle9b); checkBoxSamplesPerMeasure.setText("Samples per measurement (1F ?)");
        editTextSamplesPerMeasure = (EditText) view.findViewById(R.id.accessAxzonEditView9b); editTextSamplesPerMeasure.setInputType(InputType.TYPE_CLASS_NUMBER); editTextSamplesPerMeasure.setEnabled(false);

        textViewSsdAddress= (TextView) view.findViewById(R.id.accessAxzonTextView9d);
        checkBoxSsdAddress = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle9d); checkBoxSsdAddress.setText("? SSD Addresss (26,27)");
        editTextSsdAddress = (TextView) view.findViewById(R.id.accessAxzonEditView9d);

        textViewRtcAddress = (TextView) view.findViewById(R.id.accessAxzonTextView9e);
        checkBoxRtcAddress = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle9e); checkBoxRtcAddress.setText("? RTC Addresss (28,29)");
        editTextRtcAddress = (TextView) view.findViewById(R.id.accessAxzonEditView9e);

        textViewBatteryLevel = (TextView) view.findViewById(R.id.accessAxzonTextView999);
        checkBoxBatteryLevel = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle999); checkBoxBatteryLevel.setText("Read Battery Level (U3)");
        editTextBatteryLevel = (TextView) view.findViewById(R.id.accessAxzonEditView999);

        textViewLoggerUserState = (TextView) view.findViewById(R.id.accessAxzonTextView10);
        checkBoxLoggerUserState = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle10); checkBoxLoggerUserState.setText("Read State (U5)");
        spinnerLoggerUserState = (Spinner) view.findViewById(R.id.accessAxzonSpinner10); setupSpinner(spinnerLoggerUserState, R.array.tagAxzon_Opus_logger_state_options); spinnerLoggerUserState.setEnabled(false);

        textViewClock = (TextView) view.findViewById(R.id.accessAxzonTextView12);
        checkBoxClock = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle12); checkBoxClock.setText("Read 30-second Clock Count (U6,7)");
        editTextClock = (TextView) view.findViewById(R.id.accessAxzonEditView12);

        textViewLoggerData = (TextView) view.findViewById(R.id.accessAxzonTextView100);
        checkBoxLoggerData = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle100); checkBoxLoggerData.setText("Data (Ua0,109f)");
        editTextLoggerDataStart = (EditText) view.findViewById(R.id.accessAxzonEditView100a); editTextLoggerDataStart.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextLoggerDataLength = (EditText) view.findViewById(R.id.accessAxzonEditView100d); editTextLoggerDataLength.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextLoggerData = (TextView) view.findViewById(R.id.accessAxzonTextView101);

        textViewPcAlarm = (TextView) view.findViewById(R.id.accessAxzonTextView15);
        checkBoxPcAlarm = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle15); checkBoxPcAlarm.setText("PC Alarm (E1 ?)");
        spinnerPcAlarm = (Spinner) view.findViewById(R.id.accessAxzonSpinner15); setupSpinner(spinnerPcAlarm, R.array.tagAxzon_Opus_inactive_active_options); spinnerPcAlarm.setEnabled(false);
        checkBoxPcAlarmTamper = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle15a); checkBoxPcAlarmTamper.setText("tamper"); checkBoxPcAlarmTamper.setEnabled(false);
        checkBoxPcAlarmBattery = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle15b); checkBoxPcAlarmBattery.setText("battery"); checkBoxPcAlarmBattery.setEnabled(false);
        checkBoxPcAlarmTemperature = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle15c); checkBoxPcAlarmTemperature.setText("temperature"); checkBoxPcAlarmTemperature.setEnabled(false);
        checkBoxPcAlarmBatteryInstalled = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle15aa); checkBoxPcAlarmBatteryInstalled.setText("Battery Installed"); checkBoxPcAlarmBatteryInstalled.setEnabled(false);
        checkBoxPcAlarmBatteryConnected = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle15ab); checkBoxPcAlarmBatteryConnected.setText("Battery Connected"); checkBoxPcAlarmBatteryConnected.setEnabled(false);

        textViewLoggerEpcState = (TextView) view.findViewById(R.id.accessAxzonTextView10k);
        checkBoxLoggerEpcState = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle10k); checkBoxLoggerEpcState.setText("Read State (E1)");
        spinnerLoggerEpcState = (Spinner) view.findViewById(R.id.accessAxzonSpinner10k); setupSpinner(spinnerLoggerEpcState, R.array.tagAxzon_Opus_logger_state_options); spinnerLoggerEpcState.setEnabled(false);

        textViewReusableLogger = (TextView) view.findViewById(R.id.accessAxzonTextView14bb);
        checkBoxReusableLogger = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle14bb); checkBoxReusableLogger.setText("Reusable Logger (E1)");
        spinnerReusableLogger = (Spinner) view.findViewById(R.id.accessAxzonSpinner14bb); setupSpinner(spinnerReusableLogger, R.array.tagAxzon_Opus_inactive_active_options); spinnerReusableLogger.setEnabled(false);

        textViewSimpleSensor = (TextView) view.findViewById(R.id.accessAxzonTextView14aa);
        checkBoxSimpleSensor = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle14aa); checkBoxSimpleSensor.setText("Simple Sensor (E21)");
        spinnerSimpleSensor = (Spinner) view.findViewById(R.id.accessAxzonSpinner14aa); setupSpinner(spinnerSimpleSensor, R.array.tagAxzon_Opus_inactive_active_options); spinnerSimpleSensor.setEnabled(false);

        textViewXpcAlarm = (TextView) view.findViewById(R.id.accessAxzonTextView14);
        checkBoxXpcAlarm = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle14); checkBoxXpcAlarm.setText("XPC Alarm (E21)");
        spinnerXpcAlarm = (Spinner) view.findViewById(R.id.accessAxzonSpinner14); setupSpinner(spinnerXpcAlarm, R.array.tagAxzon_Opus_inactive_active_options); spinnerXpcAlarm.setEnabled(false);
        checkBoxXpcAlarmArmingBattery = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle14a); checkBoxXpcAlarmArmingBattery.setText("Arming Battery"); checkBoxXpcAlarmArmingBattery.setEnabled(false);
        checkBoxXpcAlarmInitialBattery = (CheckBox) view.findViewById(R.id.accessAxzonCheckBoxTitle14b); checkBoxXpcAlarmInitialBattery.setText("Initial Battery"); checkBoxXpcAlarmInitialBattery.setEnabled(false);

        buttonRead = (Button) view.findViewById(R.id.accessRWReadButton);
        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.csLibrary4A.isReaderConnected() == false) {
                    Toast.makeText(MainActivity.context, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                } else if (MainActivity.csLibrary4A.isRfidFailure()) {
                    Toast.makeText(MainActivity.context, "Rfid is disabled", Toast.LENGTH_SHORT).show();
                    return;
                }
                operationRead = true; startReadWrite();
            }
        });

        buttonWrite = (Button) view.findViewById(R.id.accessRWWriteButton);
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.csLibrary4A.isReaderConnected() == false) {
                    Toast.makeText(MainActivity.context, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                } else if (MainActivity.csLibrary4A.isRfidFailure()) {
                    Toast.makeText(MainActivity.context, "Rfid is disabled", Toast.LENGTH_SHORT).show();
                    return;
                }
                operationRead = false; startReadWrite();
            }
        });

        buttonResetLogData = (Button) view.findViewById(R.id.accessAxzonButtonResetLogData); buttonResetLogData.setText("Reset Logging Data");
        buttonResetLogData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.csLibrary4A.isReaderConnected() == false) {
                    Toast.makeText(MainActivity.context, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                } else if (MainActivity.csLibrary4A.isRfidFailure()) {
                    Toast.makeText(MainActivity.context, "Rfid is disabled", Toast.LENGTH_SHORT).show();
                    return;
                }
                String stringAlarmUpperDelayed = editTextAlarmUpperDelayed.getText().toString();
                String stringAlarmLowerDelayed = editTextAlarmLowerDelayed.getText().toString();
                int iTidAlarmPosition = spinnerTidAlarm.getSelectedItemPosition();
                MainActivity.csLibrary4A.appendToLog("AccessOpusLoggerFragment.onViewCreated: stringAlarmUpperDelayed = " + stringAlarmUpperDelayed
                        + ", stringAlarmLowerDelayed = " + stringAlarmLowerDelayed + ", iTidAlarmPosition = " + iTidAlarmPosition);
                if (stringAlarmUpperDelayed.trim().length() == 0 || stringAlarmLowerDelayed.trim().length() == 0 || iTidAlarmPosition == 0) {
                    Toast.makeText(MainActivity.context, "TID is not ready !!! Please wait to read", Toast.LENGTH_SHORT).show();
                    checkBoxPcAlarm.setChecked(true); checkBoxReusableLogger.setChecked(true); checkBoxXpcAlarm.setChecked(true);
                    checkBoxAlarmUpperDelayed.setChecked(true); checkBoxAlarmLowerDelayed.setChecked(true); checkBoxTidAlarm.setChecked(true);
                    operationRead = true; startReadWrite();
                } else {
                    checkBoxPcAlarm.setChecked(true); checkBoxXpcAlarm.setChecked(true);
                    checkBoxAlarmUpperDelayed.setChecked(true); checkBoxAlarmLowerDelayed.setChecked(true);
                    checkBoxFirstTemperatureAlarmAddress.setChecked(true); checkBoxFirstTamperAlarmAddress.setChecked(true); checkBoxFingerArmedClock.setChecked(true);
                    checkBoxTidAlarm.setChecked(true);
                    checkBoxNextLogAddress.setChecked(true);
                    operationRead = false; startReadWrite();
                }
            }
        });

        buttonStartLogging = (Button) view.findViewById(R.id.accessAxzonButtonStartLogging); buttonStartLogging.setText("Start Logging");
        buttonStartLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.csLibrary4A.isReaderConnected() == false) {
                    Toast.makeText(MainActivity.context, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                    return;
                } else if (MainActivity.csLibrary4A.isRfidFailure()) {
                    Toast.makeText(MainActivity.context, "Rfid is disabled", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spinnerReusableLogger.isEnabled()) {
                    checkBoxReusableLogger.setChecked(true); checkBoxLoggerArmedSecond.setChecked(true);
                    operationRead = false; startReadWrite();
                } else {
                    Toast.makeText(MainActivity.context, "EPC is not ready !!! Please wait to read", Toast.LENGTH_SHORT).show();
                    checkBoxReusableLogger.setChecked(true);
                    operationRead = true; startReadWrite();
                }
            }
        });

        MainActivity.csLibrary4A.setSameCheck(false);

        tagAxzonOpus = MainActivity.csLibrary4A.getTagAxzonOpus(MainActivity.sharedObjects.playerN, MainActivity.sharedObjects.playerO, buttonRead, buttonWrite);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserVisibleHint2(true);
    }

    @Override
    public void onDestroy() {
        if (MainActivity.csLibrary4A != null) MainActivity.csLibrary4A.setSameCheck(true);
        setUserVisibleHint2(false);
        mHandler.removeCallbacks(updateRunnable);
        super.onDestroy();
    }

    boolean userVisibleHint = false;
    public void setUserVisibleHint2(boolean isVisibleToUser) {
        //super.setUserVisibleHint(isVisibleToUser);
        MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.setUserVisibleHint: isVisibleToUser = " + isVisibleToUser);
        //if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED) == false) return;
        MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.setUserVisibleHint: 1 isVisibleToUser = " + isVisibleToUser);
        if (isVisibleToUser) { //getUserVisibleHint()) {
            userVisibleHint = true;
            setupTagID();
        } else {
            userVisibleHint = false;
        }
    }
    public AccessOpusLoggerFragment() {
        super("AccessXerxesLoggerFragment");
    }
    void setupTagID() {
        ReaderDevice tagSelected = MainActivity.tagSelected;
        boolean bSelected = false;
        if (tagSelected != null) {
            if (tagSelected.getSelected() == true) {
                String stringDetail = tagSelected.getDetails();
                int indexUser = stringDetail.indexOf("TID=");
                if (indexUser != -1) {
                    //
                }
                bSelected = true;
                if (editTextRWTagID != null) editTextRWTagID.setText(tagSelected.getAddress());

                if (tagSelected.getMdid() == null) {
                } else if (MainActivity.tagTypeExpected == RfidReaderData.TagType.TAG_MAGNUS_S2) {
                    modelCode = 2;
                } else if (MainActivity.tagTypeExpected == RfidReaderData.TagType.TAG_MAGNUS_S3) {
                    modelCode = 3;
                } else if (MainActivity.tagTypeExpected == RfidReaderData.TagType.TAG_AXZON_XERXES) {
                    modelCode = 5;
                } else if (MainActivity.tagTypeExpected == RfidReaderData.TagType.TAG_AXZON_OPUS) {
                    modelCode = 50;
                }

                String strRes = tagSelected.getRes();
                if (strRes != null) {
                    int ibracket = strRes.indexOf("(");
                    if (ibracket > 0) strRes = strRes.substring(0, ibracket);
                }

                stringDetail = tagSelected.getDetails();
                indexUser = stringDetail.indexOf("USER=");
                if (indexUser != -1) {
                    String stringUser = stringDetail.substring(indexUser + 5);
                    MainActivity.csLibrary4A.appendToLog("stringUser = " + stringUser);

                    boolean bEnableBAPMode = false;
                    int number = Integer.valueOf(stringUser.substring(3, 4), 16);
                    if ((number % 2) == 1) bEnableBAPMode = true;
                }
            }
        }
    }
    void startReadWrite() {
        MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.startReadWrite: updating = " + updating);
        if (updating == false) {
            TagAxzonOpus.selectData = new SelectData(editTextRWTagID.getText().toString(), editTextAccessRWAccPassword.getText().toString(), Integer.valueOf(editTextaccessRWAntennaPower.getText().toString()));
            updating = true; bankProcessing = 0;
            checkProcessing = 0;

            mHandler.removeCallbacks(updateRunnable);
            mHandler.post(updateRunnable);
        }
    }
    boolean updating = false; int bankProcessing = 0;
    int checkProcessing = 0;
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            boolean rerunRequest = true;
            CustomAsyncTask.Status status = tagAxzonOpus.getReadWriteStatus();
            if (status == null) {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: NULL stringReadWriteStatus");

                boolean invalid = processTickItems();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: processTickItems is invalid = " + invalid + ", CustomAsyncTask.status = " + tagAxzonOpus.getReadWriteStatus());
                if (invalid == true && tagAxzonOpus.getReadWriteStatus() == null) {
                    rerunRequest = false;
                } else {
                    //rerunRequest = true;
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: " + (operationRead ? "reading" : "writing") + " is started");
                }
            } else if (status == CustomAsyncTask.Status.FINISHED) {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: FINISHED accessReadWriteTask");
                if (processResult()) {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: processResult is TRUE");
                } else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: processResult is FALSE");
                }
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: stringReadWriteStatus =  " + status.toString());
            }
            if (rerunRequest) {
                mHandler.postDelayed(updateRunnable, 500);
                if (DEBUG) MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: Restart");
            }
            else updating = false;
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.updateRunnable: Ending with updating = " + updating);
        }
    };
    void setupSpinner(Spinner spinner, @ArrayRes int textArrayResId) {
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(), textArrayResId, R.layout.custom_spinner_layout);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }
    void updatePcAlarm(@NonNull TagAxzonOpus.PcAlarmTypes epcAlarmType) {
        textViewPcAlarm.setText("O");
        checkBoxPcAlarm.setChecked(false);

        spinnerPcAlarm.setSelection(epcAlarmType.isAlarm() ? 2 : 1);
        if (epcAlarmType.tamperAlarm > 0) checkBoxPcAlarmTamper.setChecked(true);
        else checkBoxPcAlarmTamper.setChecked(false);
        if (epcAlarmType.batteryAlarm > 0) checkBoxPcAlarmBattery.setChecked(true);
        else checkBoxPcAlarmBattery.setChecked(false);
        if (epcAlarmType.temperatureAlarm > 0) checkBoxPcAlarmTemperature.setChecked(true);
        else checkBoxPcAlarmTemperature.setChecked(false);
        if (epcAlarmType.batteryInstalled > 0) checkBoxPcAlarmBatteryInstalled.setChecked(true);
        else checkBoxPcAlarmBatteryInstalled.setChecked(false);
        if (epcAlarmType.batteryConnected > 0) checkBoxPcAlarmBatteryConnected.setChecked(true);
        else checkBoxPcAlarmBatteryConnected.setChecked(false);
    }
    void updateLoggerEpcState(TagAxzonOpus.LoggerStateTypes loggingStateType) {
        textViewLoggerEpcState.setText("O");
        checkBoxLoggerEpcState.setChecked(false);
        spinnerLoggerEpcState.setSelection(loggingStateType.ordinal() + 1);
    }
    void updateReusableLogger(TagAxzonOpus.DisableEnableTypes reusableLoggerType) {
        textViewReusableLogger.setText("O");
        checkBoxReusableLogger.setChecked(false);
        spinnerReusableLogger.setEnabled(true); spinnerReusableLogger.setSelection(reusableLoggerType.ordinal() + 1);
    }
    void updateXpcAlarm(@NonNull TagAxzonOpus.XpcAlarmTypes xpcAlarmType) {
        textViewXpcAlarm.setText("O");
        checkBoxXpcAlarm.setChecked(false);

        spinnerXpcAlarm.setSelection(xpcAlarmType.isAlarm() ? 2 : 1);
        if (xpcAlarmType.armingBattery > 0) checkBoxXpcAlarmArmingBattery.setChecked(true);
        else checkBoxXpcAlarmArmingBattery.setChecked(false);
        if (xpcAlarmType.initialBatteryLowAlarm > 0) checkBoxXpcAlarmInitialBattery.setChecked(true);
        else checkBoxXpcAlarmInitialBattery.setChecked(false);
    }
    void updateSimpleSensor(TagAxzonOpus.DisableEnableTypes simpleSensorType) {
        textViewSimpleSensor.setText("O");
        checkBoxSimpleSensor.setChecked(false);
        spinnerSimpleSensor.setEnabled(true); spinnerSimpleSensor.setSelection(simpleSensorType.ordinal() + 1);
    }
    void updateFingerSpotStartup(TagAxzonOpus.FingerSpotStartupEnables fingerSpotStartupType) {
        MainActivity.csLibrary4A.appendToLog("AccessOpusLoggerFragment.updateFingerSpotStartup with tamperPolarityConnected = " + fingerSpotStartupType.tamperDisconnectPolarity);
        textViewFingerSpotStartup.setText("O");
        checkBoxFingerSpotStartup.setChecked(false);

        /*spinnerFingerSpotStartup.setEnabled(true);*/ spinnerFingerSpotStartup.setSelection(fingerSpotStartupType.isEnable() ? 2 : 1);
        checkBoxFingerSpotStartEnable.setEnabled(true); if (fingerSpotStartupType.fingerSpotStartEnable > 0) checkBoxFingerSpotStartEnable.setChecked(true);
        else checkBoxFingerSpotStartEnable.setChecked(false);
        checkBoxTamperDetectEnable.setEnabled(true); if (fingerSpotStartupType.tamperDetectEnable > 0) checkBoxTamperDetectEnable.setChecked(true);
        else checkBoxTamperDetectEnable.setChecked(false);
        checkBoxTamperDisconnectPolarity.setEnabled(true); if (fingerSpotStartupType.tamperDisconnectPolarity > 0) checkBoxTamperDisconnectPolarity.setChecked(true);
        else checkBoxTamperDisconnectPolarity.setChecked(false);
        checkBoxSampingRegimeEnable.setEnabled(true); if (fingerSpotStartupType.samplingRegimeEnable > 0) checkBoxSampingRegimeEnable.setChecked(true);
        else checkBoxSampingRegimeEnable.setChecked(false);
    }
    void updateAlarmUpperLimit(float fValue) {
        textViewAlarmUpperLimit.setText("O");
        checkBoxAlarmUpperLimit.setChecked(false);
        editTextAlarmUpperLimit.setEnabled(true); editTextAlarmUpperLimit.setText(String.valueOf(fValue));
    }
    void updateAlarmUpperDelayed(int iValue) {
        textViewAlarmUpperDelayed.setText("O");
        checkBoxAlarmUpperDelayed.setChecked(false);
        editTextAlarmUpperDelayed.setText(String.valueOf(iValue));
    }
    void updateAlarmLowerLimit(float fValue) {
        textViewAlarmLowerLimit.setText("O");
        checkBoxAlarmLowerLimit.setChecked(false);
        editTextAlarmLowerLimit.setEnabled(true); editTextAlarmLowerLimit.setText(String.valueOf(fValue));
    }
    void updateAlarmLowerDelayed(int iValue) {
        textViewAlarmLowerDelayed.setText("O");
        checkBoxAlarmLowerDelayed.setChecked(false);
        editTextAlarmLowerDelayed.setText(String.valueOf(iValue));
    }
    void updateFirstTemperatureAlarmAddress(String string) {
        textViewFirstTemperatureAlarmAddress.setText("O");
        checkBoxFirstTemperatureAlarmAddress.setChecked(false);
        editTextFirstTemperatureAlarmAddress.setText(string);
    }
    void updateFirstTamperAlarmAddress(String string) {
        textViewFirstTamperAlarmAddress.setText("O");
        checkBoxFirstTamperAlarmAddress.setChecked(false);
        editTextFirstTamperAlarmAddress.setText(string);
    }
    void updateFingerArmedClock(String string) {
        textViewFingerArmedClock.setText("O");
        checkBoxFingerArmedClock.setChecked(false);
        editTextFingerArmedClock.setText(string); editTextFingerArmedClock.setEnabled(true);
    }
    void updateLoggingInterval(TagAxzonOpus.LoggingIntervalTypes loggingIntervalType) {
        textViewLoggingInterval.setText("O");
        checkBoxLoggingInterval.setChecked(false);
        spinnerLoggingInterval.setEnabled(true); spinnerLoggingInterval.setSelection(loggingIntervalType.ordinal() + 1);
    }
    void updateSamplingRegimePeriod(int iValue) {
        textViewSamplingRegimePeriod.setText("O");
        checkBoxSamplingRegimePeriod.setChecked(false);
        editTextSamplingRegimePeriod.setEnabled(true); editTextSamplingRegimePeriod.setText(String.valueOf(iValue));
    }
    void updateTidAlarm(TagAxzonOpus.TidAlarmTypes tidAlarmType) {
        textViewTidAlarm.setText("O");
        checkBoxTidAlarm.setChecked(false);

        spinnerTidAlarm.setSelection(tidAlarmType.isAlarm() ? 2 : 1);
        //checkBoxTidAlarmLowTemperature.setEnabled(true);
        if (tidAlarmType.lowTemperatureAlarm > 0) checkBoxTidAlarmLowTemperature.setChecked(true);
        else checkBoxTidAlarmLowTemperature.setChecked(false);
        //checkBoxTidAlarmHighTemperature.setEnabled(true);
        if (tidAlarmType.highTemperatureAlarm > 0) checkBoxTidAlarmHighTemperature.setChecked(true);
        else checkBoxTidAlarmHighTemperature.setChecked(false);
        //checkBoxTidAlarmTamper.setEnabled(true);
        if (tidAlarmType.tamperAlarm > 0) checkBoxTidAlarmTamper.setChecked(true);
        else checkBoxTidAlarmTamper.setChecked(false);
        //checkBoxTidAlarmBattery.setEnabled(true);
        if (tidAlarmType.batteryAlarm > 0) checkBoxTidAlarmBattery.setChecked(true);
        else checkBoxTidAlarmBattery.setChecked(false);
    }
    void updateAlarmLowerDelay(int iValue) {
        textViewAlarmLowerDelay.setText("O");
        MainActivity.csLibrary4A.appendToLog("AccessOpusLoggerFragment.updateAlarmLowerDelay: going to set checkBoxAlarmLowerDelay to false");
        checkBoxAlarmLowerDelay.setChecked(false);
        editTextAlarmLowerDelay.setEnabled(true); editTextAlarmLowerDelay.setText(String.valueOf(iValue));
    }
    void updateAlarmUpperDelay(int iValue) {
        textViewAlarmUpperDelay.setText("O");
        checkBoxAlarmUpperDelay.setChecked(false);
        editTextAlarmUpperDelay.setEnabled(true); editTextAlarmUpperDelay.setText(String.valueOf(iValue));
    }
    void updateLoggingDelayedStart(int iValue) {
        textViewLoggingDelayedStart.setText("O");
        checkBoxLoggingDelayedStart.setChecked(false);
        editTextLoggingDelayedStart.setEnabled(true); editTextLoggingDelayedStart.setText(String.valueOf(iValue));
    }
    void updateLoggerArmedSecond(String string) {
        textViewLoggerArmedSecond.setText("O");
        checkBoxLoggerArmedSecond.setChecked(false);
        editTextLoggerArmedSecond.setText(string);
    }
    void updateConfigAddress(String string) {
        textViewConfigAddress.setText("O");
        checkBoxConfigAddress.setChecked(false);
        editTextConfigAddress.setText(string);
    }
    void updateLoggingSampleSize(TagAxzonOpus.SampleNumberToLogTypes sampleNumberToLogType) {
        textViewLoggingSampleSize.setText("O");
        checkBoxLoggingSampleSize.setChecked(false);
        spinnerLoggingSampleSize.setSelection(sampleNumberToLogType.ordinal() + 1);
    }
    void updateNextLogAddress(String string) {
        textViewNextLogAddress.setText("O");
        checkBoxNextLogAddress.setChecked(false);
        editTextNextLogAddress.setText(string); editTextNextLogAddress.setEnabled(true);
    }
    void updateMinBattery4Logging(int iValue) {
        textViewMinBattery4Logging.setText("O");
        checkBoxMinBattery4Logging.setChecked(false);
        editTextMinBattery4Logging.setText(String.valueOf(iValue));
    }
    void updateMinBattery4Arming(int iValue) {
        textViewMinBattery4Arming.setText("O");
        checkBoxMinBattery4Arming.setChecked(false);
        editTextMinBattery4Arming.setText(String.valueOf(iValue));
    }
    void updateFingerSpotLed(TagAxzonOpus.DisableEnableTypes fingerSpotLedType) {
        textViewFingerSpotLed.setText("O");
        checkBoxFingerSpotLed.setChecked(false);
        spinnerFingerSpotLed.setSelection(fingerSpotLedType.ordinal() + 1);
    }
    void updateLedMode(TagAxzonOpus.DisableEnableTypes ledModeType) {
        textViewLedMode.setText("O");
        checkBoxLedMode.setChecked(false);
        spinnerLedMode.setEnabled(true); spinnerLedMode.setSelection(ledModeType.ordinal() + 1);
    }
    void updateLedOn(int iValue) {
        textViewLedOn.setText("O");
        checkBoxLedOn.setChecked(false);
        editTextLedOn.setEnabled(true); editTextLedOn.setText(String.valueOf(iValue));
    }
    void updateLedOff(int iValue) {
        textViewLedOff.setText("O");
        checkBoxLedOff.setChecked(false);
        editTextLedOff.setEnabled(true); editTextLedOff.setText(String.valueOf(iValue));
    }
    void updateBAPduration(int iValue) {
        textViewBAPduration.setText("O");
        checkBoxBAPduration.setChecked(false);
        editTextBAPduration.setEnabled(true); editTextBAPduration.setText(String.valueOf(iValue));
    }
    void updateWritePermaLock(TagAxzonOpus.DisableEnableTypes fingerSpotLedType) {
        textViewWritePermaLock.setText("O");
        checkBoxWritePermaLock.setChecked(false);
        spinnerWritePermalock.setEnabled(true); spinnerWritePermalock.setSelection(fingerSpotLedType.ordinal() + 1);
    }
    void updateSamplesPerMeasure(int iValue) {
        textViewSamplesPerMeasure.setText("O");
        checkBoxSamplesPerMeasure.setChecked(false);
        editTextSamplesPerMeasure.setEnabled(true); editTextSamplesPerMeasure.setText(String.valueOf(iValue));
    }
    void updateSsdAddress(String string) {
        textViewSsdAddress.setText("O");
        checkBoxSsdAddress.setChecked(false);
        editTextSsdAddress.setText(string);
    }
    void updateRtcAddress(String string) {
        textViewRtcAddress.setText("O");
        checkBoxRtcAddress.setChecked(false);
        editTextRtcAddress.setText(string);
    }
    void updateBatteryLevel(int iValue) {
        textViewBatteryLevel.setText("O");
        checkBoxBatteryLevel.setChecked(false);
        editTextBatteryLevel.setText(String.valueOf(iValue));
    }
    void updateLoggerUserState(TagAxzonOpus.LoggerStateTypes loggingStateType) {
        textViewLoggerUserState.setText("O");
        checkBoxLoggerUserState.setChecked(false);
        spinnerLoggerUserState.setSelection(loggingStateType.ordinal() + 1);
    }
    void updateClock(int iValue) {
        textViewClock.setText("O");
        checkBoxClock.setChecked(false);
        editTextClock.setText(String.valueOf(iValue));
    }
    void updateLoggerData(TagAxzonOpus.LoggerData[] loggerData) {
        textViewLoggerData.setText("O");
        checkBoxLoggerData.setChecked(false);
        if (loggerData != null) {
            String string = "";
            for (int i = 0; i < loggerData.length; i++) {
                string += ((loggerData[i].tamper ? "tamper," : "") + String.valueOf(loggerData[i].temperature) + "\u00B0C" + "\n");
            }
            editTextLoggerData.setText(string);
        }
    }
    boolean processResult() {
        MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processResult: readWriteTypes = " + readWriteTypes.toString());
        if (readWriteTypes == ReadWriteTypes.USERCODE_PC_ALARM) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.PcAlarmTypes epcAlarmType = tagAxzonOpus.getPcAlarmType(false);
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): epcAlarmType = " + (epcAlarmType == null ? "null" : "valid"));
            if (epcAlarmType == null) {
                textViewPcAlarm.setText("E");
                return false;
            } else {
                updatePcAlarm(epcAlarmType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LOGGER_EPC_STATE) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.LoggerStateTypes loggerStateType = tagAxzonOpus.getLoggerEpcStateType();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): loggerStateType = " + (loggerStateType == null ? "null" : loggerStateType.toString()));
            if (loggerStateType == null) {
                textViewLoggerEpcState.setText("E");
                return false;
            } else {
                updateLoggerEpcState(loggerStateType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_REUSABLE_LOGGER) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.DisableEnableTypes reusableLoggerType = tagAxzonOpus.getReusableLoggerType();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): reusableLoggerType = " + (reusableLoggerType == null ? "null" : reusableLoggerType.toString()));
            if (reusableLoggerType == null) {
                textViewReusableLogger.setText("E");
                return false;
            } else {
                updateReusableLogger(reusableLoggerType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_XPC_ALARM) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.XpcAlarmTypes xpcAlarmType = tagAxzonOpus.getXpcAlarmType(false);
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): xpcAlarmType = " + (xpcAlarmType == null ? "null" : "valid"));
            if (xpcAlarmType == null) {
                textViewXpcAlarm.setText("E");
                return false;
            } else {
                updateXpcAlarm(xpcAlarmType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_SIMPLE_SENSOR) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.DisableEnableTypes simpleSensorType = tagAxzonOpus.getSimpleSensorType();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): simpleSensorType = " + simpleSensorType.toString());
            if (simpleSensorType == null) {
                textViewSimpleSensor.setText("E");
                return false;
            } else {
                updateSimpleSensor(simpleSensorType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_FINGERSPOT_STARTUP) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.FingerSpotStartupEnables fingerSpotStartupType = tagAxzonOpus.getFingerSpotStartupEnables();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): fingerSpotType = " + (fingerSpotStartupType == null ? "null" : "valid"));
            if (fingerSpotStartupType == null) {
                textViewFingerSpotStartup.setText("E");
                return false;
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessOpusLoggerFragment.processResult: going to updateFingerSpotStartup");
                updateFingerSpotStartup(fingerSpotStartupType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_ALRAM_UPPER_LIMIT) {
            readWriteTypes = ReadWriteTypes.NULL;
            float fValue = tagAxzonOpus.getAlarmUpperLimit();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): AlarmUpperLimit = " + fValue);
            if (fValue == tagAxzonOpus.fNO_SUCH_SETTING) {
                textViewAlarmUpperLimit.setText("E");
                return false;
            } else {
                updateAlarmUpperLimit(fValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_ALARM_UPPER_DELAYED) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getAlarmUpperDelayed(false);
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): alarmUpperDelayed = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewAlarmUpperDelayed.setText("E");
                return false;
            } else {
                updateAlarmUpperDelayed(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_ALRAM_LOWER_LIMIT) {
            readWriteTypes = ReadWriteTypes.NULL;
            float fValue = tagAxzonOpus.getAlarmLowerLimit();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): AlarmLowerLimit = " + fValue);
            if (fValue == tagAxzonOpus.fNO_SUCH_SETTING) {
                textViewAlarmLowerLimit.setText("E");
                return false;
            } else {
                updateAlarmLowerLimit(fValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_ALARM_LOWER_DELAYED) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getAlarmLowerDelayed(false);
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): alarmLowerDelayed = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewAlarmLowerDelayed.setText("E");
                return false;
            } else {
                updateAlarmLowerDelayed(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_FIRST_TEMPERATURE_ALARM_ADDRESS) {
            readWriteTypes = ReadWriteTypes.NULL;
            String string = tagAxzonOpus.getFirstTemperatureAlarmAddress();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): firstTemperatureAlarmddress = " + string);
            if (string == null) {
                textViewFirstTemperatureAlarmAddress.setText("E");
                return false;
            } else {
                updateFirstTemperatureAlarmAddress(string);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_FIRST_TAMPER_ALARM_ADDRESS) {
            readWriteTypes = ReadWriteTypes.NULL;
            String string = tagAxzonOpus.getFirstTamperAlarmAddress();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): firstTamperAlarmAddress = " + string);
            if (string == null) {
                textViewFirstTamperAlarmAddress.setText("E");
                return false;
            } else {
                updateFirstTamperAlarmAddress(string);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_FINGER_ARMED_CLOCK) {
            readWriteTypes = ReadWriteTypes.NULL;
            String string = tagAxzonOpus.getFingerArmedClock();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): fingerArmedAddress = " + string);
            if (string == null) {
                textViewFingerArmedClock.setText("E");
                return false;
            } else {
                updateFingerArmedClock(string);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LOGGING_INTERVAL) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.LoggingIntervalTypes loggingIntervalType = tagAxzonOpus.getLoggingIntervalType();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): LoggingIntervalType = " + (loggingIntervalType == null ? "null" : loggingIntervalType.toString()));
            if (loggingIntervalType == null) {
                textViewLoggingInterval.setText("E");
                return false;
            } else {
                updateLoggingInterval(loggingIntervalType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_SAMPLING_REGIME_PERIOD) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getSamplingRegimePeriod();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): samplingRegimePeriod = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewSamplingRegimePeriod.setText("E");
                return false;
            } else {
                updateSamplingRegimePeriod(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_TID_ALARM) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.TidAlarmTypes tidAlarmType = tagAxzonOpus.getTidAlarmType(false);
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): epcAlarmType = " + (tidAlarmType == null ? "null" : tidAlarmType.toString()));
            if (tidAlarmType == null) {
                textViewTidAlarm.setText("E");
                return false;
            } else {
                updateTidAlarm(tidAlarmType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_ALRAM_LOWER_DELAY) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getAlarmLowerDelay();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): AlarmLowerDelay = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewAlarmLowerDelay.setText("E");
                return false;
            } else {
                updateAlarmLowerDelay(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_ALRAM_UPPER_DELAY) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getAlarmUpperDelay();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): AlarmUpperDelay = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewAlarmUpperDelay.setText("E");
                return false;
            } else {
                updateAlarmUpperDelay(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LOGGING_DELAYED_START) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getLoggingDelayedStart();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): delayedLoggingStart = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewLoggingDelayedStart.setText("E");
                return false;
            } else {
                updateLoggingDelayedStart(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LOGGER_ARMED_SECOND) {
            readWriteTypes = ReadWriteTypes.NULL;
            String string = tagAxzonOpus.getLoggerArmedSecond();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): loggerArmedClock = " + string);
            if (string == null) {
                textViewLoggerArmedSecond.setText("E");
                return false;
            } else {
                updateLoggerArmedSecond(string);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_CONFIG_ADDRESS) {
            readWriteTypes = ReadWriteTypes.NULL;
            String string = tagAxzonOpus.getConfigAddress();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): configAddress = " + string);
            if (string == null) {
                textViewConfigAddress.setText("E");
                return false;
            } else {
                updateConfigAddress(string);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_SAMPLENUMBER_TOLOG) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.SampleNumberToLogTypes sampleNumberToLogType = tagAxzonOpus.getSampleNumberToLog();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): sampleNumberToLogType = " + (sampleNumberToLogType == null ? "null" : sampleNumberToLogType.toString()));
            if (sampleNumberToLogType == null) {
                textViewLoggingSampleSize.setText("E");
                return false;
            } else {
                updateLoggingSampleSize(sampleNumberToLogType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_NEXT_LOG_ADDRESS) {
            readWriteTypes = ReadWriteTypes.NULL;
            String string = tagAxzonOpus.getNextLogAddress();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): nextLogAddress = " + string);
            if (string == null) {
                textViewNextLogAddress.setText("E");
                return false;
            } else {
                updateNextLogAddress(string);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_MINBATTERY_4LOGGING) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getMinBattery4Logging();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): minBattery4Logging = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewMinBattery4Logging.setText("E");
                return false;
            } else {
                updateMinBattery4Logging(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_MINBATTERY_4ARMING) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getMinBattery4Arming();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): minBattery4Arming = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewMinBattery4Arming.setText("E");
                return false;
            } else {
                updateMinBattery4Arming(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_FINGERSPOT_LED) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.DisableEnableTypes fingerSpotLedType = tagAxzonOpus.getFingerSpotLedType();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): fingerSpotLedType = " + fingerSpotLedType.toString());
            if (fingerSpotLedType == null) {
                textViewFingerSpotLed.setText("E");
                return false;
            } else {
                updateFingerSpotLed(fingerSpotLedType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LED_MODE) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.DisableEnableTypes ledModeType = tagAxzonOpus.getLedModeType();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): ledModeType = " + ledModeType.toString());
            if (ledModeType == null) {
                textViewLedMode.setText("E");
                return false;
            } else {
                updateLedMode(ledModeType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LED_ON) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getLedOn();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): LedOn = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewLedOn.setText("E");
                return false;
            } else {
                updateLedOn(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LED_OFF) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getLedOff();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): LedOff = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewLedOff.setText("E");
                return false;
            } else {
                updateLedOff(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_BAP_DURATION) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getBAPduration();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): BAPduration = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewBAPduration.setText("E");
                return false;
            } else {
                updateBAPduration(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_WRITE_PERMALOCK) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.DisableEnableTypes writePermaLockType = tagAxzonOpus.getWritePermaLockType();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): writePermaLockType = " + writePermaLockType.toString());
            if (writePermaLockType == null) {
                textViewWritePermaLock.setText("E");
                return false;
            } else {
                updateWritePermaLock(writePermaLockType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_SAMPLES_PERMEASURE) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getSamplesPerMeasure();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): samplesPerMeasure = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewSamplesPerMeasure.setText("E");
                return false;
            } else {
                updateSamplesPerMeasure(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_SSD_ADDRESS) {
            readWriteTypes = ReadWriteTypes.NULL;
            String string = tagAxzonOpus.getSsdAddress();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): ssdAddress = " + string);
            if (string == null) {
                textViewSsdAddress.setText("E");
                return false;
            } else {
                updateSsdAddress(string);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_RTC_ADDRESS) {
            readWriteTypes = ReadWriteTypes.NULL;
            String string = tagAxzonOpus.getRtcAddress();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): rtcAddress = " + string);
            if (string == null) {
                textViewRtcAddress.setText("E");
                return false;
            } else {
                updateRtcAddress(string);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_BATTERY_LEVEL) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getBatteryLevel(false);
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): batteryLevel = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewBatteryLevel.setText("E");
                return false;
            } else {
                updateBatteryLevel(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LOGGER_USER_STATE) {
            readWriteTypes = ReadWriteTypes.NULL;
            TagAxzonOpus.LoggerStateTypes loggerStateType = tagAxzonOpus.getLoggerUserStateType();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): loggerStateType = " + (loggerStateType == null ? "null" : loggerStateType.toString()));
            if (loggerStateType == null) {
                textViewLoggerUserState.setText("E");
                return false;
            } else {
                updateLoggerUserState(loggerStateType);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_CLOCK) {
            readWriteTypes = ReadWriteTypes.NULL;
            int iValue = tagAxzonOpus.getClock();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): RTC = " + iValue);
            if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                textViewClock.setText("E");
                return false;
            } else {
                updateClock(iValue);
                return true;
            }
        } else if (readWriteTypes == ReadWriteTypes.USERCODE_LOGGER_DATA) {
            readWriteTypes = ReadWriteTypes.NULL;
            int dataStart = Integer.parseInt(editTextLoggerDataStart.getText().toString());
            int dataLength = Integer.parseInt(editTextLoggerDataLength.getText().toString());
            TagAxzonOpus.LoggerData[] loggerData = tagAxzonOpus.getLoggerData(dataStart, dataLength);
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment().processResult(): loggerData = " + (loggerData == null ? "null" : "valid"));
            if (loggerData == null) {
                textViewLoggerData.setText("E");
                return false;
            } else {
                updateLoggerData(loggerData);
                return true;
            }
        }
        return false;
    }
    boolean processTickItems() {
        boolean invalidRequest1 = false;
        int accBank = 0, accSize = 0, accOffset = 0;
        String writeData = "";

        MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: Start, editTextRWTagID = " + editTextRWTagID.getText().toString()
                + ", checkBoxAlarmLowerDelay.isChecked = " + checkBoxAlarmLowerDelay.isChecked()
                + ", checkProcessing = " + checkProcessing
                + ", modelCode == " + modelCode);

        if (editTextRWTagID.getText().toString().length() == 0) { invalidRequest1 = true;
        } else if (checkBoxPcAlarm.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_PC_ALARM.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_PC_ALARM; checkProcessing = ReadWriteTypes.USERCODE_PC_ALARM.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.PcAlarmTypes pcAlarmType = tagAxzonOpus.getPcAlarmType(true);
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: pcAlarmType is " + (pcAlarmType == null ? "null" : pcAlarmType.toString()));
                if (pcAlarmType == null) {
                    textViewPcAlarm.setText("");
                    spinnerPcAlarm.setSelection(0);
                } else updatePcAlarm(pcAlarmType);
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with epcAlarmType = 0");
                TagAxzonOpus.PcAlarmTypes pcAlarmType = new TagAxzonOpus.PcAlarmTypes();
                if (tagAxzonOpus.setPcAlarmType(pcAlarmType)) updatePcAlarm(pcAlarmType);
                else {
                    textViewPcAlarm.setText("");
                    spinnerPcAlarm.setSelection(0);
                    checkBoxPcAlarmTamper.setChecked(false);
                    checkBoxPcAlarmBattery.setChecked(false);
                    checkBoxPcAlarmTemperature.setChecked(false);
                    checkBoxPcAlarmBatteryInstalled.setChecked(false);
                    checkBoxPcAlarmBatteryConnected.setChecked(false);
                }
            }
        } else if (checkBoxLoggerEpcState.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LOGGER_EPC_STATE.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LOGGER_EPC_STATE; checkProcessing = ReadWriteTypes.USERCODE_LOGGER_EPC_STATE.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.LoggerStateTypes loggerStateType = tagAxzonOpus.getLoggerEpcStateType();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: loggerStateType is " + (loggerStateType == null ? "null" : loggerStateType.toString()));
                if (loggerStateType == null) {
                    textViewLoggerEpcState.setText("");
                    spinnerLoggerEpcState.setSelection(0);
                } else {
                    updateLoggerEpcState(loggerStateType);
                }
            } else {
            }
        } else if (checkBoxReusableLogger.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_REUSABLE_LOGGER.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_REUSABLE_LOGGER; checkProcessing = ReadWriteTypes.USERCODE_REUSABLE_LOGGER.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.DisableEnableTypes reusableLoggerType = tagAxzonOpus.getReusableLoggerType();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: reusableLoggerType is " + (reusableLoggerType == null ? "null" : reusableLoggerType.toString()));
                if (reusableLoggerType == null) {
                    textViewReusableLogger.setText("");
                    spinnerReusableLogger.setSelection(0);
                } else updateReusableLogger(reusableLoggerType);
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with reusableLoggerType = 0");
                if (spinnerReusableLogger.getSelectedItemPosition() == 0) {
                    textViewReusableLogger.setText("E");
                    checkBoxWritePermaLock.setChecked(false);
                } else {
                    if (tagAxzonOpus.setReusableLoggerType(spinnerReusableLogger.getSelectedItemPosition() == 2)) {
                        textViewReusableLogger.setText("O");
                        checkBoxReusableLogger.setChecked(false);
                    } else {
                        textViewReusableLogger.setText("");
                        spinnerReusableLogger.setSelection(0);
                    }
                }
            }
        } else if (checkBoxXpcAlarm.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_XPC_ALARM.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_XPC_ALARM; checkProcessing = ReadWriteTypes.USERCODE_XPC_ALARM.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.XpcAlarmTypes xpcAlarmType = tagAxzonOpus.getXpcAlarmType(true);
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: xpcAlarmType is " + (xpcAlarmType == null ? "null" : xpcAlarmType.toString()));
                if (xpcAlarmType == null) {
                    textViewXpcAlarm.setText("");
                    spinnerXpcAlarm.setSelection(0);
                    checkBoxXpcAlarmArmingBattery.setChecked(false);
                    checkBoxXpcAlarmInitialBattery.setChecked(false);
                } else updateXpcAlarm(xpcAlarmType);
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with xpcAlarmType = 0");
                TagAxzonOpus.XpcAlarmTypes xpcAlarmType = new TagAxzonOpus.XpcAlarmTypes();
                if (tagAxzonOpus.setXpcAlarmType(xpcAlarmType)) updateXpcAlarm(xpcAlarmType);
                else {
                    textViewXpcAlarm.setText("");
                    spinnerXpcAlarm.setSelection(0);
                    checkBoxXpcAlarmArmingBattery.setChecked(false);
                    checkBoxXpcAlarmInitialBattery.setChecked(false);
                }
            }
        } else if (checkBoxSimpleSensor.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_SIMPLE_SENSOR.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_SIMPLE_SENSOR; checkProcessing = ReadWriteTypes.USERCODE_SIMPLE_SENSOR.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.DisableEnableTypes simpleSensorType = tagAxzonOpus.getSimpleSensorType();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: simpleSensorType is " + (simpleSensorType == null ? "null" : simpleSensorType.toString()));
                if (simpleSensorType == null) {
                    textViewSimpleSensor.setText("");
                    spinnerSimpleSensor.setSelection(0);
                } else {
                    updateSimpleSensor(simpleSensorType);
                }
            } else {
                if (spinnerSimpleSensor.getSelectedItemPosition() == 0) {
                    textViewSimpleSensor.setText("E");
                    checkBoxSimpleSensor.setChecked(false);
                } else {
                    if (tagAxzonOpus.setSimpleSensorType(spinnerSimpleSensor.getSelectedItemPosition() == 2)) {
                        textViewSimpleSensor.setText("O");
                        checkBoxSimpleSensor.setChecked(false);
                    } else {
                        textViewSimpleSensor.setText("");
                        spinnerSimpleSensor.setSelection(0);
                    }
                }
            }
        } else if (checkBoxFingerSpotStartup.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_FINGERSPOT_STARTUP.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_FINGERSPOT_STARTUP; checkProcessing = ReadWriteTypes.USERCODE_FINGERSPOT_STARTUP.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.FingerSpotStartupEnables fingerSpotStartupType = tagAxzonOpus.getFingerSpotStartupEnables();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: fingerSpotStartupType is " + (fingerSpotStartupType == null ? "null" : "valid"));
                if (fingerSpotStartupType == null) {
                    textViewFingerSpotStartup.setText("");
                    spinnerFingerSpotStartup.setSelection(0);
                } else {
                    MainActivity.csLibrary4A.appendToLog("AccessOpusLoggerFragment.processTickItems: going to updateFingerSpotStartup");
                    updateFingerSpotStartup(fingerSpotStartupType);
                }
            } else {
                if (tagAxzonOpus.getFingerSpotStartupEnables() == null) invalidRequest1 = true;
                else {
                    TagAxzonOpus.FingerSpotStartupEnables fingerSpotStartupEnables = new TagAxzonOpus.FingerSpotStartupEnables();
                    if (checkBoxFingerSpotStartEnable.isChecked())
                        fingerSpotStartupEnables.fingerSpotStartEnable = 1;
                    if (checkBoxTamperDetectEnable.isChecked())
                        fingerSpotStartupEnables.tamperDetectEnable = 1;
                    if (checkBoxTamperDisconnectPolarity.isChecked())
                        fingerSpotStartupEnables.tamperDisconnectPolarity = 1;
                    if (checkBoxSampingRegimeEnable.isChecked())
                        fingerSpotStartupEnables.samplingRegimeEnable = 1;
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with fingerSpotStartupEnables");
                    tagAxzonOpus.setFingerSpotStartupEnables(fingerSpotStartupEnables);
                    textViewFingerSpotStartup.setText("");
                    spinnerFingerSpotStartup.setSelection(0);
                }
                if (invalidRequest1) {
                    Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewFingerSpotStartup.setText("E");
                    checkBoxFingerSpotStartup.setChecked(false);
                }
            }
        } else if (checkBoxAlarmUpperLimit.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_ALRAM_UPPER_LIMIT.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_ALRAM_UPPER_LIMIT; checkProcessing = ReadWriteTypes.USERCODE_ALRAM_UPPER_LIMIT.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                float fValue = tagAxzonOpus.getAlarmUpperLimit();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: fValue is " + fValue);
                if (fValue == tagAxzonOpus.fNO_SUCH_SETTING) {
                    textViewAlarmUpperLimit.setText("");
                    editTextAlarmUpperLimit.setText("");
                } else {
                    updateAlarmUpperLimit(fValue);
                }
            } else {
                String string = editTextAlarmUpperLimit.getText().toString();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with string = " + string);
                float fValue = tagAxzonOpus.fNO_SUCH_SETTING;
                boolean bInvalid = true;
                try {
                    fValue = Float.valueOf(string);
                    bInvalid = false;
                } catch (Exception ex) { }
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with bInvalid = " + bInvalid + ", fValue = " + fValue);
                if (bInvalid) invalidRequest1 = true;
                else if (tagAxzonOpus.getAlarmUpperLimit() == tagAxzonOpus.fNO_SUCH_SETTING) invalidRequest1 = true;
                else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with alarmUpperLimit = " + fValue);
                    tagAxzonOpus.setAlarmUpperLimit(fValue);
                    textViewAlarmUpperLimit.setText("");
                    editTextAlarmUpperLimit.setText("");
                }
                if (invalidRequest1) {
                    if (bInvalid) Toast.makeText(MainActivity.context, "Invalid data", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewAlarmUpperLimit.setText("E");
                    checkBoxAlarmUpperLimit.setChecked(false);
                }
            }
        } else if (checkBoxAlarmUpperDelayed.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_ALARM_UPPER_DELAYED.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_ALARM_UPPER_DELAYED; checkProcessing = ReadWriteTypes.USERCODE_ALARM_UPPER_DELAYED.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getAlarmUpperDelayed(true);
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: alarmUpperDelayed is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewAlarmUpperDelayed.setText("");
                    editTextAlarmUpperDelayed.setText("");
                } else {
                    updateAlarmUpperDelayed(iValue);
                }
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with alarmUpperDelayed = 0");
                if (tagAxzonOpus.setAlarmUpperDelayed(0)) {
                    updateAlarmUpperDelayed(0);
                } else {
                    textViewAlarmUpperDelayed.setText("");
                    editTextAlarmUpperDelayed.setText("");
                }
            }
        } else if (checkBoxAlarmLowerLimit.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_ALRAM_LOWER_LIMIT.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_ALRAM_LOWER_LIMIT; checkProcessing = ReadWriteTypes.USERCODE_ALRAM_LOWER_LIMIT.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                float fValue = tagAxzonOpus.getAlarmLowerLimit();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: fValue is " + fValue);
                if (fValue == tagAxzonOpus.fNO_SUCH_SETTING) {
                    textViewAlarmLowerLimit.setText("");
                    editTextAlarmLowerLimit.setText("");
                } else {
                    updateAlarmLowerLimit(fValue);
                }
            } else {
                String string = editTextAlarmLowerLimit.getText().toString();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with string = " + string);
                float fValue = tagAxzonOpus.fNO_SUCH_SETTING;
                boolean bInvalid = true;
                try {
                    fValue = Float.valueOf(string);
                    bInvalid = false;
                } catch (Exception ex) { }
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with bInvalid = " + bInvalid + ", fValue = " + fValue);
                if (bInvalid) invalidRequest1 = true;
                else if (tagAxzonOpus.getAlarmLowerLimit() == tagAxzonOpus.fNO_SUCH_SETTING) invalidRequest1 = true;
                else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with alarmUpperLimit = " + fValue);
                    tagAxzonOpus.setAlarmLowerLimit(fValue);
                    textViewAlarmLowerLimit.setText("");
                    editTextAlarmLowerLimit.setText("");
                }
                if (invalidRequest1) {
                    if (bInvalid) Toast.makeText(MainActivity.context, "Invalid data", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewAlarmLowerLimit.setText("E");
                    checkBoxAlarmLowerLimit.setChecked(false);
                }
            }
        } else if (checkBoxAlarmLowerDelayed.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_ALARM_LOWER_DELAYED.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_ALARM_LOWER_DELAYED; checkProcessing = ReadWriteTypes.USERCODE_ALARM_LOWER_DELAYED.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getAlarmLowerDelayed(true);
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: alarmLowerDelayed is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewAlarmLowerDelayed.setText("");
                    editTextAlarmLowerDelayed.setText("");
                } else updateAlarmLowerDelayed(iValue);
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with alarmLowerDelayed = 0");
                if (tagAxzonOpus.setAlarmLowerDelayed(0)) {
                    updateAlarmLowerDelayed(0);
                } else {
                    textViewAlarmLowerDelayed.setText("");
                    editTextAlarmLowerDelayed.setText("");
                }
            }
        } else if (checkBoxFirstTemperatureAlarmAddress.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_FIRST_TEMPERATURE_ALARM_ADDRESS.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_FIRST_TEMPERATURE_ALARM_ADDRESS; checkProcessing = ReadWriteTypes.USERCODE_FIRST_TEMPERATURE_ALARM_ADDRESS.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                String string = tagAxzonOpus.getFirstTemperatureAlarmAddress();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: firstTemperatureAlarmAddress is " + string);
                if (string == null) {
                    textViewFirstTemperatureAlarmAddress.setText("");
                    editTextFirstTemperatureAlarmAddress.setText("");
                } else updateFirstTemperatureAlarmAddress(string);
            } else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with firstTemperatureAlarmAddress = 0");
                    if (tagAxzonOpus.setFirstTemperatureAlarmAddress(0)) {
                        updateFirstTemperatureAlarmAddress("000");
                    } else {
                        textViewFirstTemperatureAlarmAddress.setText("");
                        editTextFirstTemperatureAlarmAddress.setText("");
                    }
            }
        } else if (checkBoxFirstTamperAlarmAddress.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_FIRST_TAMPER_ALARM_ADDRESS.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_FIRST_TAMPER_ALARM_ADDRESS; checkProcessing = ReadWriteTypes.USERCODE_FIRST_TAMPER_ALARM_ADDRESS.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                String string = tagAxzonOpus.getFirstTamperAlarmAddress();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: firstTamperAlarmAddress is " + string);
                if (string == null) {
                    textViewFirstTamperAlarmAddress.setText("");
                    editTextFirstTamperAlarmAddress.setText("");
                } else updateFirstTamperAlarmAddress(string);
            } else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with firstTamperAlarmAddress = 0");
                    if (tagAxzonOpus.setFirstTamperAlarmAddress(0)) {
                        updateFirstTamperAlarmAddress("000");
                    } else {
                        textViewFirstTamperAlarmAddress.setText("");
                        editTextFirstTamperAlarmAddress.setText("");
                    }
            }
        } else if (checkBoxFingerArmedClock.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_FINGER_ARMED_CLOCK.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_FINGER_ARMED_CLOCK; checkProcessing = ReadWriteTypes.USERCODE_FINGER_ARMED_CLOCK.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                String string = tagAxzonOpus.getFingerArmedClock();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: fingerArmedAddress is " + string);
                if (string == null) {
                    textViewFingerArmedClock.setText("");
                    editTextFingerArmedClock.setText("");
                } else updateFingerArmedClock(string);
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with fingerArmedClock = 0");
                if (tagAxzonOpus.setFingerArmedClock(0)) {
                    updateFingerArmedClock("000000");
                } else {
                    textViewFingerArmedClock.setText("");
                    editTextFingerArmedClock.setText("");
                }
            }
        } else if (checkBoxLoggingInterval.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LOGGING_INTERVAL.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LOGGING_INTERVAL; checkProcessing = ReadWriteTypes.USERCODE_LOGGING_INTERVAL.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.LoggingIntervalTypes loggingIntervalType = tagAxzonOpus.getLoggingIntervalType();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: loggingIntervalType is " + (loggingIntervalType == null ? "null" : loggingIntervalType.toString()));
                if (loggingIntervalType == null) {
                    textViewLoggingInterval.setText("");
                    spinnerLoggingInterval.setSelection(0);
                } else updateLoggingInterval(loggingIntervalType);
            } else {
                int iValue = spinnerLoggingInterval.getSelectedItemPosition();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with iValue = " + iValue);
                if (iValue == 0) invalidRequest1 = true;
                else if (tagAxzonOpus.getLoggingIntervalType() == null) invalidRequest1 = true;
                else {
                    TagAxzonOpus.LoggingIntervalTypes loggingIntervalType = TagAxzonOpus.LoggingIntervalTypes.values()[iValue-1];
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with loggingIntervalType = " + loggingIntervalType.toString());
                    tagAxzonOpus.setLoggingIntervalType(loggingIntervalType);
                    textViewLoggingInterval.setText("");
                    spinnerLoggingInterval.setSelection(0);
                }
                if (invalidRequest1) {
                    if (iValue == 0) Toast.makeText(MainActivity.context, "Invalid data", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewLoggingInterval.setText("E");
                    checkBoxLoggingInterval.setChecked(false);
                }
            }
        } else if (checkBoxSamplingRegimePeriod.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_SAMPLING_REGIME_PERIOD.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_SAMPLING_REGIME_PERIOD; checkProcessing = ReadWriteTypes.USERCODE_SAMPLING_REGIME_PERIOD.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getSamplingRegimePeriod();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: iValue is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewSamplingRegimePeriod.setText("");
                    editTextSamplingRegimePeriod.setText("");
                } else {
                    updateSamplingRegimePeriod(iValue);
                }
            } else {
                String string = editTextSamplingRegimePeriod.getText().toString();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with string = " + string);
                int iValue = tagAxzonOpus.iNO_SUCH_SETTING;
                boolean bInvalid = true;
                try {
                    iValue = Integer.valueOf(string);
                    bInvalid = false;
                } catch (Exception ex) { }
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with bInvalid = " + bInvalid + ", iValue = " + iValue);
                if (bInvalid) invalidRequest1 = true;
                else if (tagAxzonOpus.getSamplingRegimePeriod() == tagAxzonOpus.fNO_SUCH_SETTING) invalidRequest1 = true;
                else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with samplingRegimePeriod = " + iValue);
                    tagAxzonOpus.setSamplingRegimePeriod(iValue);
                    textViewSamplingRegimePeriod.setText("");
                    editTextSamplingRegimePeriod.setText("");
                }
                if (invalidRequest1) {
                    if (bInvalid) Toast.makeText(MainActivity.context, "Invalid data", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewSamplingRegimePeriod.setText("E");
                    checkBoxSamplingRegimePeriod.setChecked(false);
                }
            }
        } else if (checkBoxTidAlarm.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_TID_ALARM.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_TID_ALARM; checkProcessing = ReadWriteTypes.USERCODE_TID_ALARM.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.TidAlarmTypes tidAlarmType = tagAxzonOpus.getTidAlarmType(true);
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: tidAlarmType is " + (tidAlarmType == null ? "null" : tidAlarmType.toString()));
                if (tidAlarmType == null) {
                    textViewTidAlarm.setText("");
                    spinnerTidAlarm.setSelection(0);
                    checkBoxTidAlarmBattery.setChecked(false);
                    checkBoxTidAlarmTamper.setChecked(false);
                    checkBoxTidAlarmLowTemperature.setChecked(false);
                    checkBoxTidAlarmHighTemperature.setChecked(false);
                } else updateTidAlarm(tidAlarmType);
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with tidAlarmType = 0");
                TagAxzonOpus.TidAlarmTypes tidAlarmType = new TagAxzonOpus.TidAlarmTypes();
                if (tagAxzonOpus.setTidAlarmType(tidAlarmType)) {
                    updateTidAlarm(tidAlarmType);
                } else {
                    textViewTidAlarm.setText("");
                    spinnerTidAlarm.setSelection(0);
                    checkBoxTidAlarmBattery.setChecked(false);
                    checkBoxTidAlarmTamper.setChecked(false);
                    checkBoxTidAlarmLowTemperature.setChecked(false);
                    checkBoxTidAlarmHighTemperature.setChecked(false);
                }
            }
        } else if (checkBoxAlarmLowerDelay.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_ALRAM_LOWER_DELAY.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_ALRAM_LOWER_DELAY; checkProcessing = ReadWriteTypes.USERCODE_ALRAM_LOWER_DELAY.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getAlarmLowerDelay();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: iValue is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewAlarmLowerDelay.setText("");
                    editTextAlarmLowerDelay.setText("");
                } else {
                    updateAlarmLowerDelay(iValue);
                }
            } else {
                String string = editTextAlarmLowerDelay.getText().toString();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with string = " + string);
                int iValue = tagAxzonOpus.iNO_SUCH_SETTING;
                boolean bInvalid = true;
                try {
                    iValue = Integer.valueOf(string);
                    bInvalid = false;
                } catch (Exception ex) { }
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with bInvalid = " + bInvalid + ", iValue = " + iValue);
                if (bInvalid) invalidRequest1 = true;
                else if (tagAxzonOpus.getAlarmUpperDelay() == tagAxzonOpus.iNO_SUCH_SETTING) invalidRequest1 = true;
                else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with alarmLowerDelay = " + iValue);
                    tagAxzonOpus.setAlarmLowerDelay(iValue);
                    textViewAlarmLowerDelay.setText("");
                    editTextAlarmLowerDelay.setText("");
                }
                if (invalidRequest1) {
                    if (bInvalid) Toast.makeText(MainActivity.context, "Invalid data", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewAlarmLowerDelay.setText("E");
                    MainActivity.csLibrary4A.appendToLog("AccessOpusLoggerFragment.processTickItems: going to set checkBoxAlarmLowerDelay to false");
                    checkBoxAlarmLowerDelay.setChecked(false);
                }
            }
        } else if (checkBoxAlarmUpperDelay.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_ALRAM_UPPER_DELAY.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_ALRAM_UPPER_DELAY; checkProcessing = ReadWriteTypes.USERCODE_ALRAM_UPPER_DELAY.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getAlarmUpperDelay();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: iValue is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewAlarmUpperDelay.setText("");
                    editTextAlarmUpperDelay.setText("");
                } else {
                    updateAlarmUpperDelay(iValue);
                }
            } else {
                String string = editTextAlarmUpperDelay.getText().toString();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with string = " + string);
                int iValue = tagAxzonOpus.iNO_SUCH_SETTING;
                boolean bInvalid = true;
                try {
                    iValue = Integer.valueOf(string);
                    bInvalid = false;
                } catch (Exception ex) { }
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with bInvalid = " + bInvalid + ", iValue = " + iValue);
                if (bInvalid) invalidRequest1 = true;
                else if (tagAxzonOpus.getAlarmUpperDelay() == tagAxzonOpus.iNO_SUCH_SETTING) invalidRequest1 = true;
                else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with alarmUpperDelay = " + iValue);
                    tagAxzonOpus.setAlarmUpperDelay(iValue);
                    textViewAlarmUpperDelay.setText("");
                    editTextAlarmUpperDelay.setText("");
                }
                if (invalidRequest1) {
                    if (bInvalid) Toast.makeText(MainActivity.context, "Invalid data", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewAlarmUpperDelay.setText("E");
                    checkBoxAlarmUpperDelay.setChecked(false);
                }
            }
        } else if (checkBoxLoggingDelayedStart.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LOGGING_DELAYED_START.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LOGGING_DELAYED_START; checkProcessing = ReadWriteTypes.USERCODE_LOGGING_DELAYED_START.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getLoggingDelayedStart();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: delayedLoggingStart is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewLoggingDelayedStart.setText("");
                    editTextLoggingDelayedStart.setText("");
                } else {
                    updateLoggingDelayedStart(iValue);
                }
            } else {
                String string = editTextLoggingDelayedStart.getText().toString();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with string = " + string);
                int iValue = tagAxzonOpus.iNO_SUCH_SETTING;
                boolean bInvalid = true;
                try {
                    iValue = Integer.valueOf(string);
                    bInvalid = false;
                } catch (Exception ex) { }
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with bInvalid = " + bInvalid + ", iValue = " + iValue);
                if (bInvalid) invalidRequest1 = true;
                else if (tagAxzonOpus.getLoggingDelayedStart() == tagAxzonOpus.iNO_SUCH_SETTING) invalidRequest1 = true;
                else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with delayedLoggingStart = " + iValue);
                    tagAxzonOpus.setLoggingDelayedStart(iValue);
                    textViewLoggingDelayedStart.setText("");
                    editTextLoggingDelayedStart.setText("");
                }
                if (invalidRequest1) {
                    if (bInvalid) Toast.makeText(MainActivity.context, "Invalid data", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewLoggingDelayedStart.setText("E");
                    checkBoxLoggingDelayedStart.setChecked(false);
                }
            }
        } else if (checkBoxLoggerArmedSecond.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LOGGER_ARMED_SECOND.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LOGGER_ARMED_SECOND;
            checkProcessing = ReadWriteTypes.USERCODE_LOGGER_ARMED_SECOND.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                String string = tagAxzonOpus.getLoggerArmedSecond();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: loggerArmedSecond is " + string);
                if (string == null) {
                    textViewLoggerArmedSecond.setText("");
                    editTextLoggerArmedSecond.setText("");
                } else updateLoggerArmedSecond(string);
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: setLoggerArmedSecond");
                tagAxzonOpus.setLoggerArmedSecond();
                textViewLoggingDelayedStart.setText("");
                editTextLoggingDelayedStart.setText("");
            }
        } else if (checkBoxConfigAddress.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_CONFIG_ADDRESS.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_CONFIG_ADDRESS; checkProcessing = ReadWriteTypes.USERCODE_CONFIG_ADDRESS.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                String string = tagAxzonOpus.getConfigAddress();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: configAddress is " + string);
                if (string == null) {
                    textViewConfigAddress.setText("");
                    editTextConfigAddress.setText("");
                } else {
                    updateConfigAddress(string);
                }
            } else {
            }
        } else if (checkBoxLoggingSampleSize.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_SAMPLENUMBER_TOLOG.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_SAMPLENUMBER_TOLOG; checkProcessing = ReadWriteTypes.USERCODE_SAMPLENUMBER_TOLOG.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.SampleNumberToLogTypes sampleNumberToLogType = tagAxzonOpus.getSampleNumberToLog();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: sampleNumberToLog is " + (sampleNumberToLogType == null ? "null" : sampleNumberToLogType.toString()));
                if (sampleNumberToLogType == null) {
                    textViewLoggingSampleSize.setText("");
                    spinnerLoggingSampleSize.setSelection(0);
                } else {
                    updateLoggingSampleSize(sampleNumberToLogType);
                }
            } else {
            }
        } else if (checkBoxNextLogAddress.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_NEXT_LOG_ADDRESS.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_NEXT_LOG_ADDRESS; checkProcessing = ReadWriteTypes.USERCODE_NEXT_LOG_ADDRESS.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                String string = tagAxzonOpus.getNextLogAddress();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: nextLogAddress is " + string);
                if (string == null) {
                    textViewNextLogAddress.setText("");
                    editTextNextLogAddress.setText("");
                } else updateNextLogAddress(string);
            } else {
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with nextLogAddress = 0");
                tagAxzonOpus.setNextLogAddress(0);
                textViewNextLogAddress.setText("");
                editTextNextLogAddress.setText("");
            }
        } else if (checkBoxMinBattery4Logging.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_MINBATTERY_4LOGGING.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_MINBATTERY_4LOGGING; checkProcessing = ReadWriteTypes.USERCODE_MINBATTERY_4LOGGING.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getMinBattery4Logging();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: minBattery4Logging is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewMinBattery4Logging.setText("");
                    editTextMinBattery4Logging.setText("");
                } else {
                    updateMinBattery4Logging(iValue);
                }
            } else {
                int iValue = -1;
                try {
                    iValue = Integer.valueOf(editTextMinBattery4Logging.getText().toString());
                } catch (Exception ex) { }
                if (iValue < 0) {
                    textViewMinBattery4Logging.setText("E");
                    checkBoxMinBattery4Logging.setChecked(false);
                }
                else {
                    if (tagAxzonOpus.setMinBattery4Logging(iValue)) {
                        textViewMinBattery4Logging.setText("O");
                        checkBoxMinBattery4Logging.setChecked(false);
                    } else {
                        textViewMinBattery4Logging.setText("");
                        editTextMinBattery4Logging.setText("");
                    }
                }
            }
        } else if (checkBoxMinBattery4Arming.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_MINBATTERY_4ARMING.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_MINBATTERY_4ARMING; checkProcessing = ReadWriteTypes.USERCODE_MINBATTERY_4ARMING.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getMinBattery4Arming();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: minBattery4Arming is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewMinBattery4Arming.setText("");
                    editTextMinBattery4Arming.setText("");
                } else {
                    updateMinBattery4Arming(iValue);
                }
            } else {
                int iValue = -1;
                try {
                    iValue = Integer.valueOf(editTextMinBattery4Arming.getText().toString());
                } catch (Exception ex) { }
                if (iValue < 0) {
                    textViewMinBattery4Arming.setText("E");
                    checkBoxMinBattery4Arming.setChecked(false);
                }
                else {
                    if (tagAxzonOpus.setMinBattery4Arming(iValue)) {
                        textViewMinBattery4Arming.setText("O");
                        checkBoxMinBattery4Arming.setChecked(false);
                    } else {
                        textViewMinBattery4Arming.setText("");
                        editTextMinBattery4Arming.setText("");
                    }
                }
            }
        } else if (checkBoxFingerSpotLed.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_FINGERSPOT_LED.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_FINGERSPOT_LED; checkProcessing = ReadWriteTypes.USERCODE_FINGERSPOT_LED.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.DisableEnableTypes fingerSpotLedType = tagAxzonOpus.getFingerSpotLedType();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: fingerSpotLedType is " + (fingerSpotLedType == null ? "null" : fingerSpotLedType.toString()));
                if (fingerSpotLedType == null) {
                    textViewFingerSpotLed.setText("");
                    spinnerFingerSpotLed.setSelection(0);
                } else {
                    updateFingerSpotLed(fingerSpotLedType);
                }
            } else {
            }
        } else if (checkBoxLedMode.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LED_MODE.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LED_MODE; checkProcessing = ReadWriteTypes.USERCODE_LED_MODE.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.DisableEnableTypes ledModeType = tagAxzonOpus.getLedModeType();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: fingerSpotLedType is " + (ledModeType == null ? "null" : ledModeType.toString()));
                if (ledModeType == null) {
                    textViewLedMode.setText("");
                    spinnerLedMode.setSelection(0);
                } else {
                    updateLedMode(ledModeType);
                }
            } else {
                if (spinnerLedMode.getSelectedItemPosition() == 0) {
                    textViewLedMode.setText("E");
                    checkBoxLedMode.setChecked(false);
                }
                else {
                    if (tagAxzonOpus.setLedModeType(spinnerLedMode.getSelectedItemPosition() == 2 ? TagAxzonOpus.DisableEnableTypes.ENABLE : TagAxzonOpus.DisableEnableTypes.DISABLE)) {
                        textViewLedMode.setText("O");
                        checkBoxLedMode.setChecked(false);
                    } else {
                        textViewLedMode.setText("");
                        spinnerLedMode.setSelection(0);
                    }
                }
            }
        } else if (checkBoxLedOn.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LED_ON.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LED_ON; checkProcessing = ReadWriteTypes.USERCODE_LED_ON.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getLedOn();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: LedOn is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewLedOn.setText("");
                    editTextLedOn.setText("");
                } else {
                    updateLedOn(iValue);
                }
            } else {
                int iValue = -1;
                try {
                    iValue = Integer.valueOf(editTextLedOn.getText().toString());
                } catch (Exception ex) { }
                if (iValue < 0) {
                    textViewLedOn.setText("E");
                    checkBoxLedOn.setChecked(false);
                } else {
                    if (tagAxzonOpus.setLedOn(iValue)) {
                        textViewLedOn.setText("O");
                        checkBoxLedOn.setChecked(false);
                    } else {
                        textViewLedOn.setText("");
                        editTextLedOn.setText("");
                    }
                }
            }
        } else if (checkBoxLedOff.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LED_OFF.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LED_OFF; checkProcessing = ReadWriteTypes.USERCODE_LED_OFF.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getLedOff();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: LedOff is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewLedOff.setText("");
                    editTextLedOff.setText("");
                } else {
                    updateLedOff(iValue);
                }
            } else {
                int iValue = -1;
                try {
                    iValue = Integer.valueOf(editTextLedOff.getText().toString());
                } catch (Exception ex) { }
                if (iValue < 0) {
                    textViewLedOff.setText("E");
                    checkBoxLedOff.setChecked(false);
                } else {
                    if (tagAxzonOpus.setLedOff(iValue)) {
                        textViewLedOff.setText("O");
                        checkBoxLedOff.setChecked(false);
                    } else {
                        textViewLedOff.setText("");
                        editTextLedOff.setText("");
                    }
                }
            }
        } else if (checkBoxBAPduration.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_BAP_DURATION.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_BAP_DURATION; checkProcessing = ReadWriteTypes.USERCODE_BAP_DURATION.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getBAPduration();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: BAPduration is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewBAPduration.setText("");
                    editTextBAPduration.setText("");
                } else {
                    updateBAPduration(iValue);
                }
            } else {
                int iValue = -1;
                try {
                    iValue = Integer.parseInt(editTextBAPduration.getText().toString());
                } catch (Exception ex) { }
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with iValue = " + iValue);
                if (iValue < 0) invalidRequest1 = true;
                    //else if (tagAxzonOpus.getNextLogAddress() == tagAxzonOpus.iNO_SUCH_SETTING) invalidRequest1 = true;
                else {
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: write with BAPduration = " + iValue);
                    tagAxzonOpus.setBAPduration(iValue);
                    textViewBAPduration.setText("");
                    editTextBAPduration.setText("");
                }
                if (invalidRequest1) {
                    if (iValue < 0) Toast.makeText(MainActivity.context, "Invalid data", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.context, "Reading before writing", Toast.LENGTH_SHORT).show();
                    textViewBAPduration.setText("E");
                    checkBoxBAPduration.setChecked(false);
                }
            }
        } else if (checkBoxWritePermaLock.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_WRITE_PERMALOCK.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_WRITE_PERMALOCK; checkProcessing = ReadWriteTypes.USERCODE_WRITE_PERMALOCK.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.DisableEnableTypes writePermaLockType = tagAxzonOpus.getWritePermaLockType();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: writePermaLockType is " + (writePermaLockType == null ? "null" : writePermaLockType.toString()));
                if (writePermaLockType == null) {
                    textViewWritePermaLock.setText("");
                    spinnerWritePermalock.setSelection(0);
                } else {
                    updateWritePermaLock(writePermaLockType);
                }
            } else {
                if (spinnerWritePermalock.getSelectedItemPosition() == 0) {
                    textViewWritePermaLock.setText("E");
                    checkBoxWritePermaLock.setChecked(false);
                }
                else {
                    if (tagAxzonOpus.setWritePermaLockType(spinnerWritePermalock.getSelectedItemPosition() == 2 ? TagAxzonOpus.DisableEnableTypes.ENABLE : TagAxzonOpus.DisableEnableTypes.DISABLE)) {
                        textViewWritePermaLock.setText("O");
                        checkBoxWritePermaLock.setChecked(false);
                    } else {
                        textViewWritePermaLock.setText("");
                        spinnerWritePermalock.setSelection(0);
                    }
                }
            }
        } else if (checkBoxSamplesPerMeasure.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_SAMPLES_PERMEASURE.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_SAMPLES_PERMEASURE; checkProcessing = ReadWriteTypes.USERCODE_SAMPLES_PERMEASURE.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getSamplesPerMeasure();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: samplesPerMeasure is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewSamplesPerMeasure.setText("");
                    editTextSamplesPerMeasure.setText("");
                } else {
                    updateSamplesPerMeasure(iValue);
                }
            } else {
                int iValue = -1;
                try {
                    iValue = Integer.valueOf(editTextSamplesPerMeasure.getText().toString());
                } catch (Exception ex) { }
                if (iValue < 0) {
                    textViewSamplesPerMeasure.setText("E");
                    checkBoxSamplesPerMeasure.setChecked(false);
                } else {
                    if (tagAxzonOpus.setSamplesPerMeasure(iValue)) {
                        textViewSamplesPerMeasure.setText("O");
                        checkBoxSamplesPerMeasure.setChecked(false);
                    } else {
                        textViewSamplesPerMeasure.setText("");
                        editTextSamplesPerMeasure.setText("");
                    }
                }
            }
        } else if (checkBoxSsdAddress.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_SSD_ADDRESS.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_SSD_ADDRESS; checkProcessing = ReadWriteTypes.USERCODE_SSD_ADDRESS.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                String string = tagAxzonOpus.getSsdAddress();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: ssdAddress is " + string);
                if (string == null) {
                    textViewSsdAddress.setText("");
                    editTextSsdAddress.setText("");
                } else {
                    updateSsdAddress(string);
                }
            } else {
            }
        } else if (checkBoxRtcAddress.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_RTC_ADDRESS.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_RTC_ADDRESS; checkProcessing = ReadWriteTypes.USERCODE_RTC_ADDRESS.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                String string = tagAxzonOpus.getRtcAddress();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: rtcAddress is " + string);
                if (string == null) {
                    textViewRtcAddress.setText("");
                    editTextRtcAddress.setText("");
                } else {
                    updateRtcAddress(string);
                }
            } else {
            }
        } else if (checkBoxBatteryLevel.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_BATTERY_LEVEL.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_BATTERY_LEVEL;
            checkProcessing = ReadWriteTypes.USERCODE_BATTERY_LEVEL.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getBatteryLevel(true);
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: batteryLevel is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewBatteryLevel.setText("");
                    editTextBatteryLevel.setText("");
                } else {
                    updateBatteryLevel(iValue);
                }
            } else {
            }
        } else if (checkBoxLoggerUserState.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LOGGER_USER_STATE.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LOGGER_USER_STATE; checkProcessing = ReadWriteTypes.USERCODE_LOGGER_USER_STATE.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                TagAxzonOpus.LoggerStateTypes loggerStateType = tagAxzonOpus.getLoggerUserStateType();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: loggerStateType is " + (loggerStateType == null ? "null" : loggerStateType.toString()));
                if (loggerStateType == null) {
                    textViewLoggerUserState.setText("");
                    spinnerLoggerUserState.setSelection(0);
                } else {
                    updateLoggerUserState(loggerStateType);
                }
            } else {
            }
        } else if (checkBoxClock.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_CLOCK.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_CLOCK; checkProcessing = ReadWriteTypes.USERCODE_CLOCK.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            if (operationRead) {
                int iValue = tagAxzonOpus.getClock();
                MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: RTC is " + iValue);
                if (iValue == tagAxzonOpus.iNO_SUCH_SETTING) {
                    textViewClock.setText("");
                    editTextClock.setText("");
                } else {
                    updateClock(iValue);
                }
            } else {
            }
        } else if (checkBoxLoggerData.isChecked() && checkProcessing < ReadWriteTypes.USERCODE_LOGGER_DATA.ordinal() && modelCode == 50) {
            readWriteTypes = ReadWriteTypes.USERCODE_LOGGER_DATA; checkProcessing = ReadWriteTypes.USERCODE_LOGGER_DATA.ordinal();
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: " + readWriteTypes.toString() + "-" + readWriteTypes.ordinal() + ", operationRead is " + operationRead);
            int dataStart = Integer.parseInt(editTextLoggerDataStart.getText().toString());
            int dataLength = Integer.parseInt(editTextLoggerDataLength.getText().toString());
            MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: dataStart = " + dataStart + ", dataLength = " + dataLength);
            if (operationRead) {
                if (dataStart >= 0) {
                    TagAxzonOpus.LoggerData[] loggerData = tagAxzonOpus.getLoggerData(dataStart, dataLength);
                    MainActivity.csLibrary4A.appendToLog("AccessXerxesLoggerFragment.processTickItems: loggerData is " + (loggerData == null ? "null" : "valid"));
                    if (loggerData == null) {
                        textViewLoggerData.setText("");
                    } else {
                        updateLoggerData(loggerData);
                    }
                }
            } else {
            }
        } else {
            invalidRequest1 = true;
        }
        return invalidRequest1;
    }
}
