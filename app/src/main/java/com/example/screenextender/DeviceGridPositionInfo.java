package com.example.screenextender;

import java.util.ArrayList;

public class DeviceGridPositionInfo {
    private int rows, cols;
    private ArrayList<SingleDevicePosition> device_positions;

    public DeviceGridPositionInfo(int newRows, int newCols, ArrayList<SingleDevicePosition> newDevicePositions) {
        rows = newRows;
        cols = newCols;
        device_positions = newDevicePositions;
    }

    public static class SingleDevicePosition {
        private String id;
        private int row, col;
        public SingleDevicePosition(String newId, int newRow, int newCol) {
            id = newId;
            row = newRow;
            col = newCol;
        }
    }
}
