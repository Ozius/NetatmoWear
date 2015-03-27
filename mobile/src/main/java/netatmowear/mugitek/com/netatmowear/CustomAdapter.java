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

package netatmowear.mugitek.com.netatmowear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import netatmowear.mugitek.com.netatmoapi.model.Measures;
import netatmowear.mugitek.com.netatmoapi.model.Module;

public class CustomAdapter extends BaseAdapter {
    Context mContext;
    List<Module> mModules;

    TextView mModuleNameView;
    TextView mDateView;
    TextView mTypeView;
    TextView mTemperatureView;
    TextView mMinTempView;
    TextView mMaxTempView;

    public CustomAdapter(Context context, List<Module> modules) {
        mContext = context;
        mModules = modules;
    }

    @Override
    public int getCount() {
        return mModules.size();
    }

    @Override
    public Object getItem(int position) {
        return mModules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.listview_row, null);

            mModuleNameView = (TextView) convertView.findViewById(R.id.module_name);
            mDateView = (TextView) convertView.findViewById(R.id.date);
            mTypeView = (TextView) convertView.findViewById(R.id.type);
            mTemperatureView = (TextView) convertView.findViewById(R.id.temperature);
            mMinTempView = (TextView) convertView.findViewById(R.id.min_temp);
            mMaxTempView = (TextView) convertView.findViewById(R.id.max_temp);
        }

        Module module = mModules.get(position);
        Measures measures = module.getMeasures();

        mModuleNameView.setText(module.getName());

        long beginTime = measures.getBeginTime();
        if (beginTime != 0) {
            Timestamp timestamp = new Timestamp(beginTime);
            String date = new SimpleDateFormat("dd/MM/yyyy kk:mm").format(timestamp);
            mDateView.setText("@ " + date);
        } else {
            mDateView.setText("@ " + Measures.STRING_NO_DATA);
        }

        mTemperatureView.setText("Temp. (°C): " + measures.getTemperature());

        if (module.getType().equals(Module.TYPE_THERMOSTAT)) {
            mTypeView.setText("Termostato");

            long dateMinTemp = measures.getDateMinTemp();
            if(dateMinTemp != 0) {
                Timestamp timestampMin = new Timestamp(dateMinTemp);
                String date = new SimpleDateFormat("dd/MM/yyyy kk:mm").format(timestampMin);
                mMinTempView.setText("Min: " + measures.getMinTemp() + " " + date);
                mMinTempView.setVisibility(View.VISIBLE);
            } else {
                mMinTempView.setVisibility(View.GONE);
            }

            long dateMaxTemp = measures.getDateMaxTemp();
            if(dateMaxTemp != 0) {
                Timestamp timestampMax = new Timestamp(dateMaxTemp);
                String date = new SimpleDateFormat("dd/MM/yyyy kk:mm").format(timestampMax);
                mMaxTempView.setText("Max: " + measures.getMaxTemp() + " " + date);
                mMaxTempView.setVisibility(View.VISIBLE);
            } else {
                mMaxTempView.setVisibility(View.GONE);
            }
        } else {
            if (mMinTempView.getVisibility() == View.VISIBLE) {
                mMinTempView.setVisibility(View.GONE);
                mMaxTempView.setVisibility(View.GONE);
            }

            mTypeView.setText(module.getType());
        }

        return convertView;
    }
}
