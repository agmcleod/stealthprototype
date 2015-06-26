package com.agmcleod.sp;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

/**
 * Created by aaronmcleod on 15-06-25.
 */
public class MyMath {
    public static boolean rectOverlapsCircle(Rectangle rectangle, Circle circle) {
        float [] vertices = new float[8];
        vertices[0] = rectangle.x;
        vertices[1] = rectangle.y;
        vertices[2] = rectangle.x + rectangle.width;
        vertices[3] = rectangle.y;
        vertices[4] = rectangle.x + rectangle.width;
        vertices[5] = rectangle.y + rectangle.height;
        vertices[6] = rectangle.x;
        vertices[7] = rectangle.y + rectangle.height;
        Vector2 center=new Vector2(circle.x, circle.y);
        float squareRadius=circle.radius*circle.radius;
        for (int i=0;i<vertices.length;i+=2){
            if (i==0){
                if (Intersector.intersectSegmentCircle(new Vector2(vertices[vertices.length - 2], vertices[vertices.length - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                    return true;
            } else {
                if (Intersector.intersectSegmentCircle(new Vector2(vertices[i-2], vertices[i-1]), new Vector2(vertices[i], vertices[i+1]), center, squareRadius))
                    return true;
            }
        }
        return rectangle.contains(circle.x, circle.y);
    }
}
