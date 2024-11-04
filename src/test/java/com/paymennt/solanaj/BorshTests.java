package com.paymennt.solanaj;/* This is free and unencumbered software released into the public domain. */

import org.junit.Test;
import org.near.borshj.Borsh;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

public class BorshTests {
  public static void main(String[] args) throws UnsupportedEncodingException {
    String s = "BAbFwc5jjSVn0mRosF65UdGijcxuEjSCtcZ1FJdw5ivyybWLMTP2Hn1yuEWSZUDWtktfVY4XtzHjEk0IXUe31f8gAAAAaXJvbiBlYXRlcgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAAQ0JTVFMAAAAAAMgAAABodHRwczovL2lwZnMuaW8vaXBmcy9RbVJieXY1VWlxUHd5Mm16aWFUQTNQQm5adTI2TG16MmtNb1Y0VkJBTmpjNFlHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf4BAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
//    String s = "BDzdf/EBWzBzXGhSM8C6b/eQ1zLM/4efgocrbNGaBhgLZHV9IMLT/7zGxb71JHsmjtV3oh25Op21J4iXB434QPQgAAAAQ2F0ICMzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAAAAAAAAAAAAAAAMgAAABodHRwczovL2Fyd2VhdmUubmV0L0FQbnJEWDJLVXVzdW5NQUg4ZHo3RHE1VWZiaUpLRFRyT1lUMi1QTk11RHcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEAAAA69ZqAysgmKLXDTG1n9sW7YUdypIcgIRHb++6AwdJIwgFkAQAB/gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";

    byte[] bytes = Base64.getDecoder().decode(s);

    ByteBuffer b = ByteBuffer.wrap(bytes);

    byte[] dist = new byte[1];
//    b.get(dist,0,1);
//    System.out.println("Key:" +  dist[0]);
//
//    dist = new byte[32];
//    b.get(dist,0,32);
//    System.out.println("Auth:" + new SolanaPublicKey(dist));
//
//    dist = new byte[32];
//    b.get(dist,0,32);
//    System.out.println("mint:" + new SolanaPublicKey(dist));

//    dist = new byte[32];
//    b.get(dist,0,dist.length);
//    System.out.println("name:" + new String(dist));
//
//    dist = new byte[18];
//    b.get(dist,0,dist.length);
//    System.out.println("symbol:" + new String(dist));
//
//    dist = new byte[200];
//    b.get(dist,0,dist.length);
//    System.out.println("uri:" + new String(dist));


    dist = new byte[360];
    b.get(dist,64,dist.length);
////
    Data m = Borsh.deserialize(dist,Data.class);
////
    System.out.println("name:" + m.name.trim());
    System.out.println("symbol:" + m.symbol);
    System.out.println("uri:" + m.uri);
    System.out.println(m.sellerFeeBasisPoints);
  }
  static public class Data implements Borsh {
    private String name;
    private String symbol;
    private String uri;
    private byte sellerFeeBasisPoints;
    public Data(){}
  }
  @Test
  public void roundtripPoint2Df() {
    final Point2Df point = new Point2Df(123, 456);
    assertEquals(point, Borsh.deserialize(Borsh.serialize(point), Point2Df.class));
  }

  @Test
  public void roundtripRect2Df() {
    final Point2Df topLeft = new Point2Df(-123, -456);
    final Point2Df bottomRight = new Point2Df(123, 456);
    final Rect2Df rect = new Rect2Df(topLeft, bottomRight);
    assertEquals(rect, Borsh.deserialize(Borsh.serialize(rect), Rect2Df.class));
  }

  static public class Point2Df implements Borsh {
    private float x;
    private float y;

    public Point2Df() {}

    public Point2Df(final float x, final float y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return String.format("Point2Df(%f, %f)", this.x, this.y);
    }

    @Override
    public boolean equals(final Object object) {
      if (object == null || object.getClass() != this.getClass()) return false;
      final Point2Df other = (Point2Df)object;
      return this.x == other.x && this.y == other.y;
    }
  }

  static public class Rect2Df implements Borsh {
    private Point2Df topLeft;
    private Point2Df bottomRight;

    public Rect2Df() {}

    public Rect2Df(final Point2Df topLeft, final Point2Df bottomRight) {
      this.topLeft = topLeft;
      this.bottomRight = bottomRight;
    }

    @Override
    public String toString() {
      return String.format("Rect2Df(%s, %s)", this.topLeft.toString(), this.bottomRight.toString());
    }

    @Override
    public boolean equals(final Object object) {
      if (object == null || object.getClass() != this.getClass()) return false;
      final Rect2Df other = (Rect2Df)object;
      return this.topLeft.equals(other.topLeft) && this.bottomRight.equals(other.bottomRight);
    }
  }
}
