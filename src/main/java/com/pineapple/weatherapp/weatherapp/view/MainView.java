package com.pineapple.weatherapp.weatherapp.view;

import com.pineapple.weatherapp.weatherapp.controller.WeatherService;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@SpringUI(path = "/main")
public class MainView extends UI {

    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button showWeatherButton;
    private Label currentLocationTitle;
    private Label currentTemp;
    private Label weatherDescription;
    private Label weatherMin;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label sunRiseLabel;
    private Label sunSetLabel;
    private ExternalResource img;
    private Embedded iconImage;
    private HorizontalLayout dashBoardMain;
    private HorizontalLayout mainDescriptionLayout;
    private VerticalLayout descriptionLayout;
    private VerticalLayout pressureLayout;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setUpLayout();
        setHeader();
        setLogo();
        setUpForm();
        dashBoardTitle();
        dashBoardDescription();

        cityTextField.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                searchWeather();
            }
        });

        showWeatherButton.addClickListener(event -> {
            searchWeather();
        });
    }


    public void setUpLayout() {
        iconImage = new Embedded();
        currentLocationTitle = new Label("Currently in Spokane");
        currentTemp = new Label("19F");
        weatherDescription = new Label("Description: Clear Skies");
        weatherMin = new Label("Min: 56F");
        weatherMax = new Label("Max: 89F");
        pressureLabel = new Label("Pressure: 123pa");
        humidityLabel = new Label("Humidity: 34");
        windSpeedLabel = new Label("Wind Speed: 123/hr");
        sunRiseLabel = new Label("Sunrise: ");
        sunSetLabel = new Label("Sunset: ");

        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        setContent(mainLayout);
    }

    private void setHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Weather!");
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        headerLayout.addComponents(title);
        mainLayout.addComponents(headerLayout);
    }

    private void setLogo() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Image icon = new Image(null, new ClassResource("/weather_icon.png"));
        icon.setWidth("92px");
        icon.setHeight("92px");
        logoLayout.addComponents(icon);
        mainLayout.addComponents(logoLayout);
    }

    private void setUpForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        // formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setWidthUndefined();
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        // Create the selection component
        unitSelect = new NativeSelect<>();
        unitSelect.setWidth("40px");
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(1));

        // Add TextField
        cityTextField = new TextField();
        cityTextField.setWidth("80%");

        // Add Button
        showWeatherButton = new Button();
        showWeatherButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponents(unitSelect, cityTextField, showWeatherButton);

        mainLayout.addComponents(formLayout);

    }

    private void dashBoardTitle() {
        dashBoardMain = new HorizontalLayout();
        dashBoardMain.setWidthUndefined();
        dashBoardMain.setSpacing(true);
        dashBoardMain.setMargin(true);

        currentLocationTitle.addStyleName(ValoTheme.LABEL_H2);
        currentLocationTitle.addStyleName(ValoTheme.LABEL_LIGHT);

        // Current Temp Label
        currentTemp.addStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.addStyleName(ValoTheme.LABEL_H1);
        currentTemp.addStyleName(ValoTheme.LABEL_LIGHT);

    }

    private void dashBoardDescription() {
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        // Description Vertical Layout
        descriptionLayout = new VerticalLayout();
        descriptionLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        descriptionLayout.addComponents(weatherDescription, weatherMin, weatherMax);

        // Pressure, humidity etc...
        pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        pressureLayout.addComponents(pressureLabel, humidityLabel, windSpeedLabel, sunRiseLabel, sunSetLabel);

    }


    private void searchWeather() {

        try {
            if(unitSelect.getValue().isEmpty()){
                Notification.show("Please select a type");
                return;
            }
        } catch (NullPointerException e) {
            Notification.show("Please select a type");
            return;
        }

        if (!cityTextField.getValue().equals("")) {
            updateUI();
        } else Notification.show("Please enter a city");
    }

    private void updateUI() throws JSONException {
        String city = cityTextField.getValue();
        String defaultUnit;
        String unit;


        if(unitSelect.getValue().equals("F")){
            defaultUnit = "imperial";
            unitSelect.setValue("F");
            // Degree Sign
            unit = "\u00b0"+"F";
        } else {
            defaultUnit = "metric";
            unitSelect.setValue("C");
            // Degree Sign
            unit = "\u00b0"+"C";
        }

        weatherService.setCityName(city);
        weatherService.setUnit(defaultUnit);

        currentLocationTitle.setValue("Currently in " + city);
        JSONObject myObject = weatherService.returnMainObject();
        double temp = myObject.getDouble("temp");
        currentTemp.setValue(temp + unit);

        // Get Weather min, max, humidity
        JSONObject mainObject = weatherService.returnMainObject();
        double minTemp = mainObject.getDouble("temp_min");
        double maxTemp = mainObject.getDouble("temp_max");
        int pressure = mainObject.getInt("pressure");
        int humidity = mainObject.getInt("humidity");

        // Get Wind Speed
        JSONObject windObject = weatherService.returnWindObject();
        double wind = windObject.getDouble("speed");

        // Get sunrise and sunset
        JSONObject systemObject = weatherService.returnSunSet();
        long sunRise = systemObject.getLong("sunrise") * 1000;
        long sunSet = systemObject.getLong("sunset") * 1000;

        // Setup icon image
        String iconCode = "";
        String description = "";
        JSONArray jsonArray = weatherService.returnWeatherArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObject = jsonArray.getJSONObject(i);
            description = weatherObject.getString("description");
            iconCode = weatherObject.getString("icon");
        }
        if (!iconCode.isEmpty()) {
            img = new ExternalResource("http://openweathermap.org/img/wn/" + iconCode + ".png");
            iconImage.setSource(img);
        }

        dashBoardMain.addComponents(currentLocationTitle, iconImage, currentTemp);
        mainLayout.addComponents(dashBoardMain);

        // Update Description UI
        weatherDescription.setValue("Cloudiness: " + description);
        weatherDescription.addStyleName(ValoTheme.LABEL_SUCCESS);
        weatherMin.setValue("Min: "+String.valueOf(minTemp)+" "+unit);
        weatherMax.setValue("Max: "+String.valueOf(maxTemp)+" "+unit);
        pressureLabel.setValue("Pressure: "+String.valueOf(pressure) +" hpa");
        humidityLabel.setValue("Humidity: "+String.valueOf(humidity)+" %");
        windSpeedLabel.setValue("Wind: "+String.valueOf(wind)+" m/s");
        sunRiseLabel.setValue("Sunrise: "+convertTime(sunRise));
        sunSetLabel.setValue("Sunset: "+convertTime(sunSet));

        mainDescriptionLayout.addComponents(descriptionLayout, pressureLayout);
        mainLayout.addComponent(mainDescriptionLayout);
    }

    private String convertTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd hh.mm aa");
        return dateFormat.format(new Date(time));
    }
}
