package com.example.football;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FootMap extends ImageView implements OnTouchListener {

	int ball_x;
	int ball_y;
	int _w;
	int _h;
	// ArrayList<Float> points;
	Path path1;
	Path path2;
	private Handler __h;
	private final int FRAME_RATE = 30;
	int last_x;
	int last_y;
	float[] temp;
	MediaPlayer mp;
	boolean[][] h_lines;
	boolean[][] v_lines;
	boolean[][] c1_lines;
	boolean[][] c2_lines;
	boolean turn;
	int width = 7;
	int height = 9;
	boolean goal = false;
	int time;

	ImageView turnV;

	@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
	public FootMap(Context context, AttributeSet attrs) {
		super(context, attrs);
		__h = new Handler();
		resetGame();
		setOnTouchListener(this);
		mp = MediaPlayer.create(context, R.raw.kick);
	}

	private Runnable r = new Runnable() {
		@Override
		public void run() {
			invalidate();
		}
	};

	private void resetGame() {
		path1 = new Path();
		path2 = new Path();
		h_lines = new boolean[width - 1][height];
		v_lines = new boolean[width][height - 1];
		c1_lines = new boolean[width - 1][height - 1];
		c2_lines = new boolean[width - 1][height - 1];
		ball_x = -1;
		ball_y = -1;
		goal = false;
		time = 0;
		turn = false;
	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int x = getWidth();
		int y = getHeight();
		int radius;
		radius = 3;
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.TRANSPARENT);
		canvas.drawPaint(paint);
		_w = x / width;
		_h = y / (height + 1);
		if (ball_x == -1 || ball_y == -1) {
			ball_x = _w * (width - 1) / 2 + (_w / 2);
			ball_y = _h * (height - 1) / 2 + _h;
			path1.moveTo(ball_x, ball_y);
		}
		// border lines of game field
		paint.setColor(Color.parseColor("#000000"));
		canvas.drawLine((_w / 2), _h, (_w / 2), _h * (height - 1) + _h, paint);
		canvas.drawLine((_w / 2), _h * height, _w * (width - 1) + (_w / 2), _h
				* height, paint);
		canvas.drawLine(_w * (width - 1) + (_w / 2), _h, _w * (width - 1)
				+ (_w / 2), _h * height, paint);
		canvas.drawLine((_w / 2), _h, _w * (width - 1) + (_w / 2), _h, paint);
		// Goal Lines
		canvas.drawLine((_w * ((width - 1) / 2 - 1)) + (_w / 2), (_h / 2),
				(_w * ((width - 1) / 2 + 1)) + (_w / 2), (_h / 2), paint);
		canvas.drawLine((_w * ((width - 1) / 2 - 1)) + (_w / 2), (_h / 2),
				(_w * ((width - 1) / 2 - 1)) + (_w / 2), _h, paint);
		canvas.drawLine((_w * ((width - 1) / 2 + 1)) + (_w / 2), (_h / 2),
				(_w * ((width - 1) / 2 + 1)) + (_w / 2), _h, paint);
		canvas.drawLine((_w * ((width - 1) / 2 - 1)) + (_w / 2), _h * height
				+ (_h / 2), (_w * ((width - 1) / 2 + 1)) + (_w / 2), _h
				* height + (_h / 2), paint);
		canvas.drawLine((_w * ((width - 1) / 2 - 1)) + (_w / 2), _h * height
				+ (_h / 2), (_w * ((width - 1) / 2 - 1)) + (_w / 2), _h
				* height, paint);
		canvas.drawLine((_w * ((width - 1) / 2 + 1)) + (_w / 2), _h * height
				+ (_h / 2), (_w * ((width - 1) / 2 + 1)) + (_w / 2), _h
				* height, paint);
		paint.setColor(Color.parseColor("#FFFFFF"));
		canvas.drawLine((_w * ((width - 1) / 2 - 1)) + (_w / 2), _h,
				(_w * ((width - 1) / 2 + 1)) + (_w / 2), _h, paint);
		canvas.drawLine((_w * ((width - 1) / 2 - 1)) + (_w / 2), _h * height,
				(_w * ((width - 1) / 2 + 1)) + (_w / 2), _h * height, paint);
		// points of field
		paint.setColor(Color.parseColor("#CD5C5C"));
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				canvas.drawCircle(_w * i + (_w / 2), _h * j + _h, radius, paint);
			}
		}

		// draw ball
		paint.setColor(Color.parseColor("#000000"));
		canvas.drawCircle(ball_x, ball_y, 10, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(3);
		canvas.drawPath(path1, paint);
		paint.setColor(Color.BLUE);
		canvas.drawPath(path2, paint);
		if (turnV != null) {
			if (turn)
				turnV.setBackgroundColor(Color.BLUE);
			else
				turnV.setBackgroundColor(Color.GREEN);
		}
		if (goal) {
			time++;
			if (time < 20) {
				if (time % 2 == 0)
					paint.setColor(Color.BLACK);
				else
					paint.setColor(Color.RED);
				paint.setTextSize(time * 2.5f);
				canvas.drawText("GOOOOAL", x / 2 - time * 5, y / 2 - time,
						paint);
			} else if (time > 60) {
				resetGame();
			} else {
				paint.setColor(Color.BLACK);
				paint.setTextSize(50);
				canvas.drawText("GOOOOAL", x / 2 - 20 * 5, y / 2 - 20, paint);
			}
		}

		__h.postDelayed(r, FRAME_RATE);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!goal) {
			View parent = (View) v.getParent();
			final TextView _x = (TextView) parent.findViewById(R.id.x);
			final TextView _y = (TextView) parent.findViewById(R.id.y);
			turnV = (ImageView) parent.findViewById(R.id.turn);
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				_x.setText(String.valueOf(x));
				_y.setText(String.valueOf(y));
				checkDirection(x, y);
			}
		}
		return true;
	}

	public void checkDirection(int x, int y) {
		boolean moved = false;
		int next_x = -1;
		int next_y = -1;
		if (Math.abs(ball_x - x) < _w * 1.1 && Math.abs(ball_y - y) < _h * 1.1) {
			if ((y < (_h * 0.8) || y > (_h * height + (_h * 0.2)))
					&& (ball_x - (_w / 2)) / _w == (width - 1) / 2) {
				goal = true;
				return;
			}
			if (Math.abs(ball_x - x) < _w * 0.5) {
				next_x = ball_x;
				if (y - ball_y > _h * 0.5) {
					next_y = ball_y + _h;
					moved = true;
				} else if (y - ball_y < _h * -0.5) {
					next_y = ball_y - _h;
					moved = true;
				}
			} else if (x - ball_x < 0) {
				next_x = ball_x - _w;
				next_y = ball_y;
				moved = true;
				if (y - ball_y > _h * 0.5) {
					next_y = ball_y + _h;
				} else if (y - ball_y < _h * -0.5) {
					next_y = ball_y - _h;
				}
			} else {
				next_x = ball_x + _w;
				next_y = ball_y;
				moved = true;
				if (y - ball_y > _h * 0.5) {
					next_y = ball_y + _h;
				} else if (y - ball_y < _h * -0.5) {
					next_y = ball_y - _h;
				}
			}
		}
		if (moved) {
			validateMove(next_x, next_y);
			mp.start();
		}
	}

	public void validateMove(int x, int y) {
		int p_i = (ball_x - (_w / 2)) / _w;
		int p_j = (ball_y - _h) / _h;
		int n_i = (x - (_w / 2)) / _w;
		int n_j = (y - _h) / _h;
		if ((p_i == 0 && n_i == 0)
				|| (p_i == (width - 1) && n_i == (width - 1)) || n_i >= width
				|| n_j >= height || n_i < 0 || n_j < 0)
			return;
		boolean changeTurn = checkTurn(n_i, n_j);
		if ((p_j == 0 && n_j == 0)
				|| (p_j == (height - 1) && n_j == (height - 1))) {
			if (p_i == (width - 1) / 2 && n_i == (width - 1) / 2 + 1)
				h_lines[p_i][p_j] = true;
			else if (p_i == (width - 1) / 2 && n_i == (width - 1) / 2 - 1)
				h_lines[n_i][p_j] = true;
			else if (p_i == (width - 1) / 2 - 1 && n_i == (width - 1) / 2)
				h_lines[p_i][p_j] = true;
			else if (p_i == (width - 1) / 2 + 1 && n_i == (width - 1) / 2)
				h_lines[n_i][p_j] = true;
			else
				return;
		} else if (p_i == n_i && p_j != n_j) {
			int tt;
			if (p_j < n_j)
				tt = p_j;
			else
				tt = n_j;
			if (!v_lines[p_i][tt])
				v_lines[p_i][tt] = true;
			else
				return;
		} else if (p_j == n_j && p_i != n_i) {
			int tt;
			if (p_i < n_i)
				tt = p_i;
			else
				tt = n_i;
			if (!h_lines[tt][p_j])
				h_lines[tt][p_j] = true;
			else
				return;
		} else {
			if (p_i < n_i && p_j < n_j) {
				if (!c1_lines[p_i][p_j])
					c1_lines[p_i][p_j] = true;
				else
					return;
			} else if (p_i > n_i && p_j > n_j) {
				if (!c1_lines[n_i][n_j])
					c1_lines[n_i][n_j] = true;
				else
					return;
			} else if (p_i > n_i && p_j < n_j) {
				if (!c2_lines[n_i][p_j])
					c2_lines[n_i][p_j] = true;
				else
					return;
			} else if (p_i < n_i && p_j > n_j) {
				if (!c2_lines[p_i][n_j])
					c2_lines[p_i][n_j] = true;
				else
					return;
			}
		}
		ball_x = x;
		ball_y = y;
		if (!turn)
			path1.lineTo(ball_x, ball_y);
		else
			path2.lineTo(ball_x, ball_y);
		mp.start();
		if (!changeTurn) {
			if (turn) {
				turn = false;
				path1.moveTo(ball_x, ball_y);
			} else {
				turn = true;
				path2.moveTo(ball_x, ball_y);
			}
		}
		checkMoveExists(n_i, n_j);

	}

	// TODO: change flag = ... to return ...
	private void checkMoveExists(int x, int y) {
		boolean ret = false;
		if (x > 0 && x < width - 1 && y > 0 && y < height - 1) {
			if (h_lines[x - 1][y] && h_lines[x][y] && v_lines[x][y - 1]
					&& v_lines[x][y] && c1_lines[x - 1][y - 1]
					&& c1_lines[x][y] && c2_lines[x][y - 1]
					&& c2_lines[x - 1][y])
				ret = true;
		} else if (x == 0) {
			if (y == 0 || y == height - 1)
				ret = true;
			else if (h_lines[x][y] && c2_lines[x][y - 1] && c1_lines[x][y])
				ret = true;
		} else if (x == width - 1) {
			if (y == 0 || y == height - 1)
				ret = true;
			else if (h_lines[x-1][y] && c2_lines[x - 1][y] && c1_lines[x-1][y-1])
				ret = true;
		} else if (y == 0) {
			if (x == (width - 1) / 2 - 1) {
				if (h_lines[x][y] && v_lines[x][y] && c1_lines[x][y] && c2_lines[x-1][y])
					ret = true;
			} else if (x == (width - 1) / 2 + 1) {
				if (h_lines[x-1][y] && v_lines[x][y] && c1_lines[x][y] && c2_lines[x-1][y])
					ret = true;
			} else if (x > 0 && x < width - 1) {
				if (v_lines[x][y] && c1_lines[x][y] && c2_lines[x-1][y])
					ret = true;
			}
		} else if(y == height - 1) {
			if (x == (width - 1) / 2 - 1) {
				if (h_lines[x][y] && v_lines[x][y-1] && c1_lines[x-1][y-1] && c2_lines[x][y-1])
					ret = true;
			} else if (x == (width - 1) / 2 + 1) {
				if (h_lines[x-1][y] && v_lines[x][y-1] && c1_lines[x-1][y-1] && c2_lines[x][y-1])
					ret = true;
			} else if (x > 0 && x < width - 1) {
				if (v_lines[x][y-1] && c1_lines[x-1][y-1] && c2_lines[x][y-1])
					ret = true;
			}
		}
		if (ret)
			goal = true;
	}

	// TODO: change flag = ... to return ...
	public boolean checkTurn(int x, int y) {
		boolean flag = false;
		if (x == 0 || (y == 0 && x != (width - 1) / 2) || x == (width - 1)
				|| (y == (height - 1) && x != (width - 1) / 2))
			return true;
		if (x > 0) {
			if (h_lines[x - 1][y])
				flag = true;
			if (y > 0 && c1_lines[x - 1][y - 1])
				flag = true;
			if (y < (height - 2) && c2_lines[x - 1][y])
				flag = true;
		}
		if (x < (width - 1)) {
			if (h_lines[x][y])
				flag = true;
			if (y < (height - 2) && c1_lines[x][y])
				flag = true;
			if (y > 0 && c2_lines[x][y - 1])
				flag = true;
		}
		if (y > 0)
			if (v_lines[x][y - 1])
				flag = true;
		if (y < (height - 2))
			if (v_lines[x][y])
				flag = true;

		return flag;
	}
}