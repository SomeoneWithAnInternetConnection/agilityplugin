package internetperson.plugins;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Model;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Region;
import net.runelite.api.Renderable;
import net.runelite.api.Tile;
import net.runelite.api.model.Jarvis;
import net.runelite.api.model.Triangle;
import net.runelite.api.model.Vertex;
import net.runelite.client.plugins.pestcontrol.Game;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
@Slf4j
public class AgilityOverlay extends Overlay
{
	private final Client client;
	private final AgilityPluginConfiguration config;

	private static final int[] faladorIds = {10833, 10834, 10836, 11161, 11360, 11361, 11364, 11365, 11366, 11367, 11368, 11370, 11371};
	//private static final int[] faladorIds = {6948};
	private static final int REGION_SIZE = 104; //Stolen from devtools

	@Inject
	public AgilityOverlay(@Nullable Client client, AgilityPluginConfiguration config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		this.client = client;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics, java.awt.Point parent)
	{
		if (!config.enabled()) return null;
		if (!config.clickboxes() && !config.wireframes() && ! config.AABBs()) return null;
		Region region = client.getRegion();
		Tile[][][] tiles = region.getTiles();

		int z = client.getPlane();

		for (int x = 0; x < REGION_SIZE; ++x)
		{
			for (int y = 0; y < REGION_SIZE; ++y)
			{
				// Z, X, Y. *NOT* X, Y, Z
				Tile tile = tiles[z][x][y];
				if (tile == null)
				{
					continue;
				}

				GameObject[] gameObjects = tile.getGameObjects();
				if (gameObjects != null)
				{
					for (GameObject object : gameObjects)
					{
						if (object != null && IntStream.of(faladorIds).anyMatch(id -> id == object.getId()))
						{
							renderClickbox(graphics, object);
							if(config.AABBs()) renderAABB(graphics, object);
						}
					}
				}
			}
		}

		return null;
	}

	private void renderAABB(Graphics2D graphics, GameObject object)
	{
		Model model = object.getModel();
		if (model == null)
		{
			return;
		}
		
		int maxX = 0;
		int maxY = 0;
		int maxZ = 0;
		int minX = 0;
		int minY = 0;
		int minZ = 0;
		boolean first = true;
		for (Vertex vertex : model.getVertices())
		{
			int x = vertex.getX();
			int y = vertex.getY();
			int z = vertex.getZ();
			if (first)
			{
				maxX = minX = x;
				maxY = minY = y;
				maxZ = minZ = z;
				first = false;
				continue;
			}

			if (x > maxX) maxX = x;
			if (x < minX) minX = x;

			if (y > maxY) maxY = y;
			if (y < minY) minY = y;

			if (z > maxZ) maxZ = z;
			if (z < minZ) minZ = z;
		}
		int centreX = (minX + maxX)/2;
		int centreY = (minY + maxY)/2;
		int centreZ = (minZ + maxZ)/2 ;

		int extremeX = (maxX - minX +1)/2;
		int extremeY = (maxY - minY +1)/2;
		int extremeZ = (maxZ - minZ +1)/2;
		
		int x1 = object.getX() - (centreX - extremeX)-32;
		int y1 = centreY - extremeY;
		int z1 = object.getY() - (centreZ - extremeZ);

		int x2 = object.getX() - (centreX + extremeX)-32;
		int y2 = centreY + extremeY;
		int z2 = object.getY() - (centreZ + extremeZ);

		Point p1 = worldToCanvas(client,
				x1, z1, -y1,
				object.getX(), object.getY());
		Point p2 = worldToCanvas(client,
				x1, z2, -y1,
				object.getX(), object.getY());
		Point p3 = worldToCanvas(client,
				x2, z2, -y1,
				object.getX(), object.getY());
		Point p4 = worldToCanvas(client,
				x2, z1, -y1,
				object.getX(), object.getY());

		Point p5 = worldToCanvas(client,
				x1, z1, -y2,
				object.getX(), object.getY());
		Point p6 = worldToCanvas(client,
				x1, z2, -y2,
				object.getX(), object.getY());
		Point p7 = worldToCanvas(client,
				x2, z2, -y2,
				object.getX(), object.getY());
		Point p8 = worldToCanvas(client,
				x2, z1, -y2,
				object.getX(), object.getY());

		graphics.setColor(Color.YELLOW);
		if (p1 != null) graphics.drawRect(p1.getX() - 1, p1.getY() - 1, 2, 2);
		if (p2 != null) graphics.drawRect(p2.getX() - 1, p2.getY() - 1, 2, 2);
		if (p3 != null) graphics.drawRect(p3.getX() - 1, p3.getY() - 1, 2, 2);
		if (p4 != null) graphics.drawRect(p4.getX() - 1, p4.getY() - 1, 2, 2);

		if (p5 != null) graphics.drawRect(p5.getX() - 1, p5.getY() - 1, 2, 2);
		if (p6 != null) graphics.drawRect(p6.getX() - 1, p6.getY() - 1, 2, 2);
		if (p7 != null) graphics.drawRect(p7.getX() - 1, p7.getY() - 1, 2, 2);
		if (p8 != null) graphics.drawRect(p8.getX() - 1, p8.getY() - 1, 2, 2);
		try
		{
			graphics.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
			graphics.drawLine(p2.getX(), p2.getY(), p3.getX(), p3.getY());
			graphics.drawLine(p3.getX(), p3.getY(), p4.getX(), p4.getY());
			graphics.drawLine(p4.getX(), p4.getY(), p1.getX(), p1.getY());

			graphics.drawLine(p1.getX(), p1.getY(), p5.getX(), p5.getY());
			graphics.drawLine(p2.getX(), p2.getY(), p6.getX(), p6.getY());
			graphics.drawLine(p3.getX(), p3.getY(), p7.getX(), p7.getY());
			graphics.drawLine(p4.getX(), p4.getY(), p8.getX(), p8.getY());

			graphics.drawLine(p5.getX(), p5.getY(), p6.getX(), p6.getY());
			graphics.drawLine(p6.getX(), p6.getY(), p7.getX(), p7.getY());
			graphics.drawLine(p7.getX(), p7.getY(), p8.getX(), p8.getY());
			graphics.drawLine(p8.getX(), p8.getY(), p5.getX(), p5.getY());
		}
		catch (NullPointerException e) {} //Shhh

	}

	private void renderClickbox(Graphics2D graphics, GameObject object)
	{
		int radius = 5;
		Model model = object.getModel();
		if (model == null)
		{
			return;
		}

		List<Triangle> triangles = model.getTriangles();
		int orientation = (object.getOrientation() + 1024) % 2048;

		if (orientation != 0)
		{
			triangles = rotate(triangles, orientation);
		}
		// Boop
		for (Triangle triangle : triangles)
		{
			Vertex _a = triangle.getA();
			Point a = worldToCanvas(client,
					object.getX() - _a.getX(),
					object.getY() - _a.getZ(),
					-_a.getY(), object.getX(), object.getY());
			if (a == null) continue;
			Vertex _b = triangle.getB();
			Point b = worldToCanvas(client,
					object.getX() - _b.getX(),
					object.getY() - _b.getZ(),
					-_b.getY(), object.getX(), object.getY());
			if (b == null) continue;
			Vertex _c = triangle.getC();
			Point c = worldToCanvas(client,
					object.getX() - _c.getX(),
					object.getY() - _c.getZ(),
					-_c.getY(), object.getX(), object.getY());
			if (c == null) continue;
			graphics.setColor(Color.green);
			int[] xs = {a.getX(), b.getX(), c.getX()};
			int[] ys = {a.getY(), b.getY(), c.getY()};
			Polygon tri = new Polygon(xs, ys, 3);
			if (config.wireframes())
			{
				graphics.drawRect(a.getX()-1, a.getY()-1, 2, 2);
				graphics.drawRect(b.getX()-1, b.getY()-1, 2, 2);
				graphics.drawRect(c.getX()-1, c.getY()-1, 2, 2);
				graphics.drawPolygon(tri);
			}

			int topleftX = Math.min(Math.min(a.getX(), b.getX()), c.getX());
			int topleftY = Math.min(Math.min(a.getY(), b.getY()), c.getY());
			int botrightX = Math.max(Math.max(a.getX(), b.getX()), c.getX()) + 4;
			int botrightY = Math.max(Math.max(a.getY(), b.getY()), c.getY()) + 4;
			if (!client.isResized())
			{
				topleftX += 4;
				topleftY += 4;
				botrightX += 4;
				botrightY += 4;
			}

			graphics.setColor(Color.CYAN);
			if (config.clickboxes()) graphics.drawRect(topleftX-radius, topleftY-radius, botrightX-topleftX+radius, botrightY-topleftY+radius);

		}

	}

	private List<Triangle> rotate(List<Triangle> triangles, int orientation)
	{
		List<Triangle> rotatedTriangles = new ArrayList<Triangle>();
		for (Triangle triangle : triangles)
		{
			Vertex a = triangle.getA();
			Vertex b = triangle.getB();
			Vertex c = triangle.getC();

			Triangle rotatedTriangle = new Triangle(
					a.rotate(orientation),
					b.rotate(orientation),
					c.rotate(orientation)
			);
			rotatedTriangles.add(rotatedTriangle);
		}
		return rotatedTriangles;
	}

	private Point worldToCanvas(Client client, int x, int y, int plane, int tilex, int tiley)
	{
		if (x >= 128 && y >= 128 && x <= 13056 && y <= 13056)
		{
			int z = Perspective.getTileHeight(client, tilex, tiley, client.getPlane()) - plane;
			x -= client.getCameraX();
			y -= client.getCameraY();
			z -= client.getCameraZ();

			int cameraPitch = client.getCameraPitch();
			int cameraYaw = client.getCameraYaw();

			int pitchSin = Perspective.SINE[cameraPitch];
			int pitchCos = Perspective.COSINE[cameraPitch];
			int yawSin = Perspective.SINE[cameraYaw];
			int yawCos = Perspective.COSINE[cameraYaw];

			int var8 = yawCos * x + y * yawSin >> 16;
			y = yawCos * y - yawSin * x >> 16;
			x = var8;
			var8 = pitchCos * z - y * pitchSin >> 16;
			y = z * pitchSin + y * pitchCos >> 16;

			if (y >= 50)
			{
				int pointX = client.getViewportHeight() / 2 + x * client.getScale() / y;
				int pointY = var8 * client.getScale() / y + client.getViewportWidth() / 2;
				return new Point(pointX, pointY);
			}
		}

		return null;

	}

}
