package com.example.projectsmartmetro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Holder> {

    //지하철 검색 목록을 보여줄 리싸이클러 뷰 구현

    List<Station> data;

    public CustomAdapter(List<Station> data) {
        //생성자로 지하철 데이터를 가져온다
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //리스트뷰의 getView와 같다, 지하철 레이아웃을 뷰로 제공한다

        //인플레이팅, layout안에 list_subway를 가져옴
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_subway, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        //데이터를 한개씩 꺼내고
        Station station = data.get(position);

        //ViewHolder에 있는 요소들을 세팅
        holder.textLaneName.setText(station.laneName);
        holder.textStationName.setText(station.stationName);
        holder.textStationCity.setText(station.cityName);
        holder.textStationGu.setText(station.gu);
        holder.textStationDong.setText(station.dong);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //리사이클러 뷰의 아이템을 선택했을 시
                Station stationSelected = data.get(position);
                //StationSearch(지하철 세부정보) Activity로 stationID를 가지고 넘어감
                Intent intent = new Intent(view.getContext(), StationInfo.class);
                intent.putExtra("stationID", stationSelected.stationID);
                view.getContext().startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {

        //지하철 데이터의 총 갯수 리턴, 스크롤의 갯수 결정
        return data.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        //아이템 레이아웃에서 사용되는 요소들을 미리 정의
        TextView textLaneName, textStationName, textStationCity, textStationGu, textStationDong;

        public Holder(View itemView) {
            super(itemView);

            //생성자에서 findViewById로 연결
            textLaneName = itemView.findViewById(R.id.textLaneName);
            textStationName = itemView.findViewById(R.id.textStationName);
            textStationCity = itemView.findViewById(R.id.textStationCity);
            textStationGu = itemView.findViewById(R.id.textStationGu);
            textStationDong = itemView.findViewById(R.id.textStationDong);


        }
    }
}

