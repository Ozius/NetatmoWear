/*
 * Copyright 2013 Netatmo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package netatmowear.mugitek.com.netatmoapi.model;

public class Measures {
    public static final String STRING_NO_DATA = "No data";

    long beginTime;
    String temperature;
    String minTemp;
    String maxTemp;
    private long dateMinTemp;
    private long dateMaxTemp;

    public Measures() {
        this.beginTime = 0;
        this.temperature = STRING_NO_DATA;
        this.dateMinTemp = 0;
        this.dateMaxTemp = 0;
    }

    public long getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public long getDateMinTemp() {
        return dateMinTemp;
    }

    public void setDateMinTemp(long dateMinTemp) {
        this.dateMinTemp = dateMinTemp;
    }

    public long getDateMaxTemp() {
        return dateMaxTemp;
    }

    public void setDateMaxTemp(long dateMaxTemp) {
        this.dateMaxTemp = dateMaxTemp;
    }
}
