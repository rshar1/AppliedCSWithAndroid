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

package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private int steps;
    private PuzzleBoard previousBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {

        steps = 0;
        tiles = new ArrayList<>();

        bitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, false);

        int tileWidth = bitmap.getWidth() / NUM_TILES;
        int tileHeight = bitmap.getHeight() / NUM_TILES;


        for (int x = 0; x < NUM_TILES; x++) {
            for (int y = 0; y < NUM_TILES; y++) {
                Bitmap tileBitmap = Bitmap.createBitmap(bitmap, tileWidth * y, tileHeight * x, tileWidth, tileHeight);
                int tileNumber = x * NUM_TILES + y;
                PuzzleTile tile = new PuzzleTile(tileBitmap, tileNumber);
                tiles.add(tile);
            }
        }

        tiles.set(tiles.size() - 1, null);

    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;

    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    Log.d("PuzzleBoard", "Number: " + tile.getNumber());
                    Log.d("PuzzleBoard", "Index: " + i);
                    Log.d("PuzzleBoard", "X: " + (i % NUM_TILES) + "Y: " + (i / NUM_TILES));
                    Log.d("PuzzleBoard", "MD: " + manhattanDistance(i, tile.getNumber()));
                    Log.d("PuzzleBoard", "solved: " + resolved());
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {

        ArrayList<PuzzleBoard> neighbours = new ArrayList<>();

        // Find the null value
        int index = 0;
        while (tiles.get(index) != null) {
            index++;
        }

        int nullX = index % NUM_TILES;
        int nullY = index / NUM_TILES;

        for (int[] delta: NEIGHBOUR_COORDS) {

            int nX = nullX + delta[0];
            int nY = nullY + delta[1];

            if (nX >= 0 && nY >= 0 && nX < NUM_TILES && nY < NUM_TILES) {
                PuzzleBoard neighbourBoard = new PuzzleBoard(this);
                neighbourBoard.swapTiles(index, XYtoIndex(nX, nY));
                neighbours.add(neighbourBoard);
            }

        }

        return neighbours;
    }

    private int manhattanDistance(int index, int number) {

        int currentX = index % NUM_TILES;
        int currentY = index / NUM_TILES;

        int targetX = number % NUM_TILES;
        int targetY = number / NUM_TILES;

        int dX = Math.abs(targetX - currentX);
        int dY = Math.abs(targetY - currentY);

        return dX + dY;

    }

    public int priority() {
        int manhattanDistance = 0;

        for (int i = 0; i < tiles.size(); i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                manhattanDistance += manhattanDistance(i, tile.getNumber());
            }
        }

        return manhattanDistance + steps;
    }

    public PuzzleBoard getPreviousBoard() {
        return this.previousBoard;
    }

}
