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

package com.google.engedu.touringmusician;


import android.graphics.Point;

import java.util.Iterator;

public class CircularLinkedList implements Iterable<Point> {

    private class Node {
        Point point;
        Node prev, next;
    }

    Node head;

    public void insertBeginning(Point p) {

        Node toBeAdded = new Node();
        toBeAdded.point = p;
        if (head == null) {
            toBeAdded.next = toBeAdded;
            toBeAdded.prev = toBeAdded;
        } else {
            head.prev.next = toBeAdded;
            toBeAdded.prev = head.prev;

            head.prev = toBeAdded;
            toBeAdded.next = head;
        }

        head = toBeAdded;
    }

    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }

    public float totalDistance() {
        float total = 0;

        Node current = head;

        while (current.next != head) {
            total += distanceBetween(current.point, current.next.point);
            current = current.next;
        }

        return total;
    }

    public void insertNearest(Point p) {

        Node toBeAdded = new Node();
        toBeAdded.point = p;

        if (head == null) {
            toBeAdded.next = toBeAdded;
            toBeAdded.prev = toBeAdded;
            head = toBeAdded;
        } else {
            Node minNode = head;
            float minDistance = distanceBetween(head.point, p);
            Node current = head.next;
            while (current != head) {
                float currentDistance = distanceBetween(current.point, p);
                if (currentDistance < minDistance) {
                    minNode = current;
                    minDistance = currentDistance;
                }
                current = current.next;
            }
            minNode.next.prev = toBeAdded;
            toBeAdded.next = minNode.next;

            minNode.next = toBeAdded;
            toBeAdded.prev = minNode;
        }

    }

    public void insertSmallest(Point p) {

        Node toBeAdded = new Node();
        toBeAdded.point = p;

        if (head == null) {
            toBeAdded.next = toBeAdded;
            toBeAdded.prev = toBeAdded;
            head = toBeAdded;
        } else {
            Node minNode = head;
            float minDifference = (distanceBetween(head.point, p) + distanceBetween(p, head.next.point)) - distanceBetween(head.point, head.next.point);
            Node current = head.next;
            while (current != head) {
                float currentDifference = (distanceBetween(current.point, p) + distanceBetween(p, current.next.point)) - distanceBetween(current.point, current.next.point);
                if (currentDifference < minDifference) {
                    minNode = current;
                    minDifference = currentDifference;
                }
                current = current.next;
            }
            minNode.next.prev = toBeAdded;
            toBeAdded.next = minNode.next;

            minNode.next = toBeAdded;
            toBeAdded.prev = minNode;
        }


    }

    public void reset() {
        head = null;
    }

    private class CircularLinkedListIterator implements Iterator<Point> {

        Node current;

        public CircularLinkedListIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Point next() {
            Point toReturn = current.point;
            current = current.next;
            if (current == head) {
                current = null;
            }
            return toReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return new CircularLinkedListIterator();
    }

}
