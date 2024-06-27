package com.example.cheesechase

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import kotlin.random.Random


class MainActivity2 : ComponentActivity() {
    private lateinit var movingCircleView: MovingCircleView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        movingCircleView = MovingCircleView(this)
        setContentView(movingCircleView)
        generateObstacles()
        val mainHandler = Handler(Looper.getMainLooper())
        var delayMillis = 1L // 1 millisecond delay


        mainHandler.post(object : Runnable {
            override fun run() {
                movingCircleView.moveCircle()
                mainHandler.postDelayed(this, delayMillis)
            }
        })
    }

    var t = 0f
    var text1 = 0
    private val obstaclesLane1 = mutableListOf<Pair<Float, Float>>()
    private val obstaclesLane2 = mutableListOf<Pair<Float, Float>>()
    private val obstaclesLane3 = mutableListOf<Pair<Float, Float>>()


    fun generateObstacles() {
        var o1 = 1500f
        var o2 = 1650f
        var o3 = 1000f
        var o4 = 1150f
        var o5 = 1250f
        var o6 = 1400f
        obstaclesLane1.add(Pair(o1,o2))
        obstaclesLane2.add(Pair(o3,o4))
        obstaclesLane3.add(Pair(o5,o6))
        for (i in 1..87) {
                o1 -= Random.nextFloat() * 10 + 800f
                o2 = o1 + 150f
                o3 -= Random.nextFloat() * 10 + 800f
                o4 = o3 + 150f
                o5 -= Random.nextFloat() * 10 + 800f
                o6 = o5 + 150f

                val lanes = listOf(1, 2, 3).shuffled().take(2)
                if (1 in lanes) {
                    obstaclesLane1.add(Pair(o1, o2))
                }
                if (2 in lanes) {
                    obstaclesLane2.add(Pair(o3, o4))
                }
                if (3 in lanes) {
                    obstaclesLane3.add(Pair(o5, o6))
                }
            }
        }

    inner class MovingCircleView(context: Context) : View(context) {
        val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val allEntries: Map<String, *> = sharedPref.all

        private val circlePaint = Paint().apply {
            color = getResources().getColor(R.color.indigo)
            style = Paint.Style.FILL
        }
        private val paint = Paint().apply {
            color = Color.CYAN
            style = Paint.Style.FILL
        }
        private val obPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }
        private val tomPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 120f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        private val scorep = Paint().apply {
            color = ContextCompat.getColor(this@MainActivity2,R.color.pink)
        }
        private val scoretext = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 60f
            textAlign = Paint.Align.CENTER
        }
        private var text = "FINISH"
        private val finishp = Paint().apply {
            color = Color.BLACK
        }
        private val backgroundPaint = Paint().apply{
            color = ContextCompat.getColor(this@MainActivity2,R.color.purp)
        }

        private var top = 2035.5f
        private var bottom = 2164.5f
        private var left = 472f
        private var right = 601f
        private var r1 = mutableListOf<Float>()
        private var r2 = mutableListOf<Float>()
        private var r3 = mutableListOf<Float>()
        private var top1 = 2800f
        private var bot1 = 3000f
        private var lef1 = 436.5f
        private var rig1 = 636.5f
        private var score1 = 2950f
        private var score2 = 3050f

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
            val player = RectF(left, top, right, bottom)
            val tom = RectF(lef1, top1, rig1, bot1)
            val finish = RectF(0f, -69345f, 1100f, -69090f)
            val score = RectF(145f,score1,285f,score2)
            val cameraY = player.centerY() - height / 2
            canvas.translate(0f, -cameraY) //wow the 0f worked

            canvas.drawRect(107f, -69000f, 340f, 4000f, paint)
            canvas.drawRect(420f, -69000f, 653f, 4000f, paint)
            canvas.drawRect(733f, -69000f, 966f, 4000f, paint)
            val linep = Paint()
            linep.color = Color.BLACK
            linep.strokeWidth = 3f
            for(i in (1..608)){
                val n = (120*i).toFloat()
                val m = 4000f - n
                canvas.drawLine(107f,m,340f,m,linep)
            }
            for(i in (1..608)){
                val n = (120*i).toFloat()
                val m = 4000f - n
                canvas.drawLine(420f,m,653f,m,linep)
            }
            for(i in (1..608)){
                val n = (120*i).toFloat()
                val m = 4000f - n
                canvas.drawLine(733f,m,966f,m,linep)
            }


            for (obstacle in obstaclesLane1) {
                canvas.drawRect(147f, obstacle.first, 300f, obstacle.second, obPaint)
                r1.add(obstacle.second)
            }

            for (obstacle in obstaclesLane2) {
                canvas.drawRect(460f, obstacle.first, 613f, obstacle.second, obPaint)
                r2.add(obstacle.second)
            }

            for (obstacle in obstaclesLane3) {
                canvas.drawRect(773f, obstacle.first, 926f, obstacle.second, obPaint)
                r3.add(obstacle.second)
            }



            canvas.drawOval(player, circlePaint)
            canvas.drawOval(tom, tomPaint)
            canvas.drawRect(score,scorep)
            val x1 = score.centerX()
            val y1 = score.centerY() - (scoretext.descent() + scoretext.ascent()) / 2
            canvas.drawText(text1.toString(), x1, y1, scoretext)
            canvas.drawRect(finish, finishp)
            val x = finish.centerX()
            val y = finish.centerY() - (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(text, x, y, textPaint)
        }

        fun moveCircle() {
            val inflater = LayoutInflater.from(context)
            if (t <= 1f) {
                for (r in r1) {
                    if (left == 159f && top <= r && top >= r - 150f) {
                        t += 1f
                        if (t == 1f) {
                            score1-=150f
                            score2-=150f
                            Toast.makeText(
                                context,
                                "Obstacle hit! Speed reduced.",
                                Toast.LENGTH_SHORT
                            ).show()
                            bottom = r - 150f
                            top = r - 279f
                            break
                        } else if (t == 2f) {
                            top1 = bottom
                            bot1 = top1 + 200f
                            val builder =
                                AlertDialog.Builder(context, R.style.AlertDialogCustom).create()
                            val view = inflater.inflate(R.layout.activity_finish, null)
                            builder.setView(view)
                            builder.setCanceledOnTouchOutside(false)
                            builder.setCancelable(false)
                            val scoret = view.findViewById<TextView>(R.id.score)
                            scoret.setText("SCORE: "+text1)
                            val again = view.findViewById<Button>(R.id.again)
                            again.setOnClickListener {
                                val animationZoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                again.startAnimation(animationZoomIn)
                                val animationZoomOut = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                again.startAnimation(animationZoomOut)
                                val intent = Intent(this@MainActivity2,MainActivity2::class.java)
                                startActivity(intent)
                            }
                            val back = view.findViewById<Button>(R.id.front)
                            back.setOnClickListener {
                                val animationZoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                back.startAnimation(animationZoomIn)
                                val animationZoomOut = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                back.startAnimation(animationZoomOut)
                                if (allEntries.isEmpty()) {
                                    editor.putInt("high",text1)
                                    editor.apply()
                                }
                                else{
                                    val high = sharedPref.getInt("high",1)
                                    if(text1>high){
                                        editor.putInt("high",text1)
                                        editor.apply()
                                    }
                                }
                                if(sharedPref.getInt("high",1) == text1){
                                    val intent = Intent(this@MainActivity2,HighScore::class.java)
                                    startActivity(intent)
                                }
                                else {
                                    val intent = Intent(this@MainActivity2, FrontPage::class.java)
                                    startActivity(intent)
                                }
                            }
                            builder.show()
                        }
                        break
                    }
                }
                for (r in r2) {
                    if (left == 472f && top <= r && top >= r - 150f) {
                        t += 1f
                        if (t == 1f) {
                            score1-=150f
                            score2-=150f
                            Toast.makeText(
                                context,
                                "Obstacle hit! Speed reduced.",
                                Toast.LENGTH_SHORT
                            ).show()
                            bottom = r - 150f
                            top = r - 279f
                            break
                        } else if (t == 2f) {
                            top1 = bottom
                            bot1 = top1 + 200f
                            val builder =
                                AlertDialog.Builder(context, R.style.AlertDialogCustom).create()
                            val view = inflater.inflate(R.layout.activity_finish, null)
                            builder.setView(view)
                            builder.setCanceledOnTouchOutside(false)
                            builder.setCancelable(false)
                            val score = view.findViewById<TextView>(R.id.score)
                            score.setText("SCORE: "+text1)
                            val again = view.findViewById<Button>(R.id.again)
                            again.setOnClickListener {
                                val animationZoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                again.startAnimation(animationZoomIn)
                                val animationZoomOut = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                again.startAnimation(animationZoomOut)
                                val intent = Intent(this@MainActivity2,MainActivity2::class.java)
                                startActivity(intent)
                            }
                            val back = view.findViewById<Button>(R.id.front)
                            back.setOnClickListener {
                                val animationZoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                back.startAnimation(animationZoomIn)
                                val animationZoomOut = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                back.startAnimation(animationZoomOut)
                                if (allEntries.isEmpty()) {
                                    editor.putInt("high",text1)
                                    editor.apply()
                                }
                                else{
                                    val high = sharedPref.getInt("high",1)
                                    if(text1>high){
                                        editor.putInt("high",text1)
                                        editor.apply()
                                    }
                                }
                                if(sharedPref.getInt("high",1) == text1){
                                    val intent = Intent(this@MainActivity2,HighScore::class.java)
                                    startActivity(intent)
                                }
                                else {
                                    val intent = Intent(this@MainActivity2, FrontPage::class.java)
                                    startActivity(intent)
                                }
                            }
                            builder.show()
                        }
                        break
                    }
                }
                for (r in r3) {
                    if (left == 785f && top <= r && top >= r - 150f) {
                        t += 1f
                        if (t == 1f) {
                            score1-=150f
                            score2-=150f
                            Toast.makeText(
                                context,
                                "Obstacle hit! Speed reduced.",
                                Toast.LENGTH_SHORT
                            ).show()
                            bottom = r - 150f
                            top = r - 279f
                            break
                        } else if (t == 2f) {
                            top1 = bottom
                            bot1 = top1 + 200f
                            val builder =
                                AlertDialog.Builder(context, R.style.AlertDialogCustom).create()
                            val view = inflater.inflate(R.layout.activity_finish, null)
                            builder.setView(view)
                            builder.setCanceledOnTouchOutside(false)
                            builder.setCancelable(false)
                            val score = view.findViewById<TextView>(R.id.score)
                            score.setText("SCORE: "+text1)
                            val again = view.findViewById<Button>(R.id.again)
                            again.setOnClickListener {
                                val animationZoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                again.startAnimation(animationZoomIn)
                                val animationZoomOut = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                again.startAnimation(animationZoomOut)
                                val intent = Intent(this@MainActivity2,MainActivity2::class.java)
                                startActivity(intent)
                            }
                            val back = view.findViewById<Button>(R.id.front)
                            back.setOnClickListener {
                                val animationZoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                back.startAnimation(animationZoomIn)
                                val animationZoomOut = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                back.startAnimation(animationZoomOut)
                                if (allEntries.isEmpty()) {
                                    editor.putInt("high",text1)
                                    editor.apply()
                                }
                                else{
                                    val high = sharedPref.getInt("high",1)
                                    if(text1>high){
                                        editor.putInt("high",text1)
                                        editor.apply()
                                    }
                                }
                                if(sharedPref.getInt("high",1) == text1){
                                    val intent = Intent(this@MainActivity2,HighScore::class.java)
                                    startActivity(intent)
                                }
                                else {
                                    val intent = Intent(this@MainActivity2, FrontPage::class.java)
                                    startActivity(intent)
                                }
                            }
                            builder.show()
                        }
                        break
                    }
                }

            }
            if(top <= -69090 && top>=-69345f){
                val builder =
                    AlertDialog.Builder(context, R.style.AlertDialogCustom).create()
                val view = inflater.inflate(R.layout.activity_finish, null)
                builder.setView(view)
                builder.setCanceledOnTouchOutside(false)
                builder.setCancelable(false)
                val score = view.findViewById<TextView>(R.id.score)
                score.setText("SCORE: "+text1)
                val again = view.findViewById<Button>(R.id.again)
                again.setOnClickListener {
                    val animationZoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                    again.startAnimation(animationZoomIn)
                    val animationZoomOut = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                    again.startAnimation(animationZoomOut)
                    val intent = Intent(this@MainActivity2,MainActivity2::class.java)
                    startActivity(intent)
                }
                val back = view.findViewById<Button>(R.id.front)
                back.setOnClickListener {
                    val animationZoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                    back.startAnimation(animationZoomIn)
                    val animationZoomOut = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                    back.startAnimation(animationZoomOut)
                    if (allEntries.isEmpty()) {
                        editor.putInt("high",text1)
                        editor.apply()
                    }
                    else{
                        val high = sharedPref.getInt("high",1)
                        if(text1>high){
                            editor.putInt("high",text1)
                            editor.apply()
                        }
                    }
                    if(sharedPref.getInt("high",1) == text1){
                        val intent = Intent(this@MainActivity2,HighScore::class.java)
                        startActivity(intent)
                    }
                    else {
                        val intent = Intent(this@MainActivity2, FrontPage::class.java)
                        startActivity(intent)
                    }
                }
                builder.show()
            }
            val intent = intent
            val f = intent.getIntExtra("message_key1",1)


            if(f==1) {
                if (t == 0f && top > -69090f) {
                    top -= 22f
                    bottom -= 22f
                    top1 -= 22f
                    bot1 -= 22f
                    score1 -= 22f
                    score2 -= 22f
                    text1 += 3
                } else if (t == 1f && top > -69090f) {//wow this works for stop
                    top -= 19f
                    bottom -= 19f
                    top1 -= 19.333f
                    bot1 -= 19.333f
                    score1 -= 19f
                    score2 -= 19f
                    text1 += 1
                }
                invalidate()
            }
            else if(f==0) {
                if (t == 0f && top > -23030f) {
                    top -= 18f
                    bottom -= 18f
                    top1 -= 18f
                    bot1 -= 18f
                    score1 -= 18f
                    score2 -= 18f
                    text1 += 2
                } else if (t == 0f && top > -46060f) {
                    top -= 20f
                    bottom -= 20f
                    top1 -= 20f
                    bot1 -= 20f
                    score1 -= 20f
                    score2 -= 20f
                    text1 += 3
                } else if (t == 0f && top > -69090f) {
                    top -= 22f
                    bottom -= 22f
                    top1 -= 22f
                    bot1 -= 22f
                    score1 -= 22f
                    score2 -= 22f
                    text1 += 4
                }
                if (t == 1f && top > -69090f) {//wow this works for stop
                    top -= 16f
                    bottom -= 16f
                    top1 -= 16.333f
                    bot1 -= 16.333f
                    score1 -= 16f
                    score2 -= 16f
                    text1 += 1
                }
                invalidate()
            }
        }

        var soundPool: SoundPool = SoundPool.Builder().setMaxStreams(4).build()
        var beepSoundId: Int = soundPool.load(context,R.raw.change, 1)
        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    if (event.x in (420f..653f)) {
                        soundPool.play(beepSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                        left = 472f
                        right = 601f
                        lef1 = 436.5f
                        rig1 = 636.5f
                    } else if (event.x in (733f..966f)) {
                        soundPool.play(beepSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                        left = 785f
                        right = 914f
                        lef1 = 749.5f
                        rig1 = 949.5f
                    } else if (event.x in (107f..340f)) {
                        soundPool.play(beepSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                        left = 159f
                        right = 289f
                        lef1 = 123.5f
                        rig1 = 323.5f
                    }

                }
            }
            invalidate()
            return true
        }
    }
    override fun onBackPressed() {
        val intent = Intent(applicationContext, FrontPage::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}