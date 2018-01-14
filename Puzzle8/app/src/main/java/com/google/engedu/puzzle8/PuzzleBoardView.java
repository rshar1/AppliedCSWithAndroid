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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {

            for (int shuffle = 0; shuffle < NUM_SHUFFLE_STEPS; shuffle++) {
                ArrayList<PuzzleBoard> neighbors = puzzleBoard.neighbours();
                int randNeighborIndex = (int) (Math.random() * neighbors.size());
                PuzzleBoard randNeighbor = neighbors.get(randNeighborIndex);
                puzzleBoard = randNeighbor;
            }

            puzzleBoard.reset();
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {


        PriorityQueue<PuzzleBoard> priorityQueue = new PriorityQueue<>(10, new Comparator<PuzzleBoard>() {
            @Override
            public int compare(PuzzleBoard puzzleBoard, PuzzleBoard t1) {
                return Integer.compare(puzzleBoard.priority(), t1.priority());
            }
        });

        priorityQueue.add(puzzleBoard);

        while (!priorityQueue.isEmpty() && !puzzleBoard.resolved()) {
            PuzzleBoard next = priorityQueue.poll();

            if (next.resolved()) {
                ArrayList<PuzzleBoard> toSolution = new ArrayList<>();

                while (next != puzzleBoard) {
                    toSolution.add(next);
                    next = next.getPreviousBoard();
                }

                Collections.reverse(toSolution);
                this.animation = toSolution;
                invalidate();
                return;

            } else {

                for (PuzzleBoard board: next.neighbours()) {
                    if (!board.equals(next)) priorityQueue.add(board);
                }

            }


        }


    }
}
