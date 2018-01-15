/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.continentaldivide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class ContinentMap extends View {
    public static final int MAX_HEIGHT = 255;
    private Cell[] map;
    private int boardSize;
    private Random random = new Random();
    private int maxHeight = 7, minHeight = 1;

    private Integer[] DEFAULT_MAP = {
            1, 2, 3, 4, 5,
            2, 3, 4, 0, 6,
            3, 4, 0, 3, 1,
            6, 7, 0, 4, 5,
            5, 1, 2, 3, 4,
    };

    public ContinentMap(Context context) {
        super(context);

        boardSize = (int) (Math.sqrt(DEFAULT_MAP.length));
        map = new Cell[boardSize * boardSize];
        for (int i = 0; i < boardSize * boardSize; i++) {
            map[i] = new Cell();
            map[i].height = DEFAULT_MAP[i];
        }
        maxHeight = Collections.max(Arrays.asList(DEFAULT_MAP));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    private class Cell {
        protected int height = 0;
        protected boolean flowsNW = false;
        protected boolean flowsSE = false;
        protected boolean basin = false;
        protected boolean processing = false;
    }

    private Cell getMap(int x, int y) {
        if (x >=0 && x < boardSize && y >= 0 && y < boardSize)
            return map[x + boardSize * y];
        else
            return null;
    }

    public void clearContinentalDivide() {
        for (int i = 0; i < boardSize * boardSize; i++) {
            map[i].flowsNW = false;
            map[i].flowsSE = false;
            map[i].basin = false;
            map[i].processing = false;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int shadeStep = 128 / (maxHeight - minHeight);
        int cellWidth = canvas.getWidth() / boardSize;
        int cellHeight = canvas.getHeight() / boardSize;
        Paint textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(50);
        textPaint.setARGB(255, 0,0,0);

        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                Cell cell = getMap(x, y);
                Paint color = new Paint();
                int shade = 255 - (shadeStep * cell.height);

                if (cell.flowsNW && cell.flowsSE) {
                    color.setARGB(255, shade, 0, 0);
                } else if (cell.flowsNW) {
                    color.setARGB(255, 0, shade, 0);
                } else if (cell.flowsSE) {
                    color.setARGB(255, 0, 0, shade);
                } else {
                    // color a shade of grey

                    Log.d("map", "Height" + cell.height + " Shade" + shade);
                    color.setARGB(255, shade, shade, shade);
                }

                float left = x * cellWidth;
                float top = y * cellHeight;
                float bottom = (y + 1) * cellHeight;
                float right = (x + 1) * cellWidth;


                canvas.drawRect(left, top, right, bottom, color);
                canvas.drawText(Integer.toString(cell.height), (left + right) / 2.0f, (top + bottom) / 2.0f, textPaint);

            }
        }

        Log.d("Continental Map", "onDraw: Completed");
    }

    public void buildUpContinentalDivide(boolean oneStep) {
        if (!oneStep)
            clearContinentalDivide();
        boolean iterated = false;
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                Cell cell = getMap(x, y);
                if ((x == 0 || y == 0 || x == boardSize - 1 || y == boardSize - 1)) {
                    buildUpContinentalDivideRecursively(
                            x, y, x == 0 || y == 0, x == boardSize - 1 || y == boardSize - 1, -1);
                    if (oneStep) {
                        iterated = true;
                        break;
                    }
                }
            }
            if (iterated && oneStep)
                break;
        }
        invalidate();
    }

    private void buildUpContinentalDivideRecursively(
            int x, int y, boolean flowsNW, boolean flowsSE, int previousHeight) {

        Cell cell = getMap(x, y);

        if (cell == null) return;

        if (cell.height < previousHeight) return;

        if (cell.processing) return;
        cell.processing = true;

        cell.flowsNW = flowsNW || cell.flowsNW;
        cell.flowsSE = flowsSE || cell.flowsSE;

        buildUpContinentalDivideRecursively(x + 1, y, flowsNW, flowsSE, cell.height);
        buildUpContinentalDivideRecursively(x - 1, y, flowsNW, flowsSE, cell.height);
        buildUpContinentalDivideRecursively(x, y + 1, flowsNW, flowsSE, cell.height);
        buildUpContinentalDivideRecursively(x, y - 1, flowsNW, flowsSE, cell.height);

        cell.processing = false;
    }

    public void buildDownContinentalDivide(boolean oneStep) {
        if (!oneStep)
            clearContinentalDivide();
        while (true) {
            int maxUnprocessedX = -1, maxUnprocessedY = -1, foundMaxHeight = -1;
            for (int y = 0; y < boardSize; y++) {
                for (int x = 0; x < boardSize; x++) {
                    Cell cell = getMap(x, y);
                    if (!(cell.flowsNW || cell.flowsSE || cell.basin) && cell.height > foundMaxHeight) {
                        maxUnprocessedX = x;
                        maxUnprocessedY = y;
                        foundMaxHeight = cell.height;
                    }
                }
            }
            if (maxUnprocessedX != -1) {
                buildDownContinentalDivideRecursively(maxUnprocessedX, maxUnprocessedY, foundMaxHeight + 1);
                if (oneStep) {
                    break;
                }
            } else {
                break;
            }
        }
        invalidate();
    }

    private Cell buildDownContinentalDivideRecursively(int x, int y, int previousHeight) {
        Cell workingCell = new Cell();
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        return workingCell;
    }

    public void generateTerrain(int detail) {
        int newBoardSize = (int) (Math.pow(2, detail) + 1);
        if (newBoardSize != boardSize * boardSize) {
            boardSize = newBoardSize;
            map = new Cell[boardSize * boardSize];
            for (int i = 0; i < boardSize * boardSize; i++) {
                map[i] = new Cell();
            }
        }
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        invalidate();
    }
}
