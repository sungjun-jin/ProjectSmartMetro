package com.example.projectsmartmetro;

public class Station {

    //역 정보 데이터 클래스

    //역 이름
    String stationName;
    //도시
    String cityName;
    //구
    String gu;
    //동
    String dong;
    //호선
    String laneName;
    //역의 주소 -> 지하철 상세정보(StationInfo)에서 사용
    String address;
    //역의 전화번호 -> 지하철 상세정보(StationInfo)에서 사용
    String tel;

    //정류장 ID
    int stationID;

    //정류장 경위도
    double longitude;
    double latitude;
}
