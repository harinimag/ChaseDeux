package com.example.chasedeux

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.random.Random


class MainActivity2 : ComponentActivity() {
    private lateinit var movingCircleView: MovingCircleView
    private var obstacleLimit: Int? = null // Declare as a member property
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

    var t = 0
    var text1 = 0
    private val obstaclesLane1 = mutableListOf<Pair<Float, Float>>()
    private val obstaclesLane2 = mutableListOf<Pair<Float, Float>>()
    private val obstaclesLane3 = mutableListOf<Pair<Float, Float>>()

    data class ObstacleLimitResponse(val obstacleLimit: Int)
    interface ChaseApi {
        @GET("/obstacleLimit")
        fun getObstacleLimit(): Call<ObstacleLimitResponse>
    }

    interface ImageApiService {
        @GET("/image")
        fun getImageByCharacter(@Query("character") characterName: String): Call<ResponseBody>
    }




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

        val retrofit = Retrofit.Builder()
            .baseUrl("https://chasedeux.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val chaseApi = retrofit.create(ChaseApi::class.java)

        val retrofit1 = Retrofit.Builder()
            .baseUrl("https://chasedeux.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val imageApiService = retrofit1.create(ImageApiService::class.java)



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
        var dr1 = 0
        var dr2 = 0
        var dr3 = 0
        var tomd: Drawable? = null
        var jerd: Drawable? = null
        var obd: Drawable? = null




        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val callt = imageApiService.getImageByCharacter("tom")
            callt.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        if(dr1 == 0) {
                            val tombyt = response.body()?.bytes()
                            val tomimg = BitmapFactory.decodeByteArray(tombyt, 0, tombyt?.size ?: 0)
                            tomd = BitmapDrawable(resources, tomimg)
                            dr1 = 1
                        }
                    } else {
                        Toast.makeText(context, "error in receiving response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "error in receiving response", Toast.LENGTH_SHORT).show()
                }
            })


            val callj = imageApiService.getImageByCharacter("jerry")
            callj.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        if(dr2 == 0) {
                            val jerbyt = response.body()?.bytes()
                            val jerimg = BitmapFactory.decodeByteArray(jerbyt, 0, jerbyt?.size ?: 0)
                            jerd = BitmapDrawable(resources, jerimg)
                            dr2 = 1
                        }
                    } else {
                        Toast.makeText(context, "error in receiving response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "error in receiving response", Toast.LENGTH_SHORT).show()
                }
            })

            val callo = imageApiService.getImageByCharacter("obstacle")
            callo.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        if(dr3 == 0) {
                            val obbyt = response.body()?.bytes()
                            val obimg = BitmapFactory.decodeByteArray(obbyt, 0, obbyt?.size ?: 0)
                            obd = BitmapDrawable(resources, obimg)
                            dr3 = 1
                        }
                    } else {
                        Toast.makeText(context, "error in receiving response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "error in receiving response", Toast.LENGTH_SHORT).show()
                }
            })


            val player = RectF(left, top, right, bottom)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

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
                obd?.setBounds(147, obstacle.first.toInt(), 300, obstacle.second.toInt())
                obd?.draw(canvas)
                r1.add(obstacle.second)
            }

            for (obstacle in obstaclesLane2) {
                obd?.setBounds(460, obstacle.first.toInt(), 613, obstacle.second.toInt())
                obd?.draw(canvas)
                r2.add(obstacle.second)
            }

            for (obstacle in obstaclesLane3) {
                obd?.setBounds(773, obstacle.first.toInt(), 926, obstacle.second.toInt())
                obd?.draw(canvas)
                r3.add(obstacle.second)
            }

            tomd?.setBounds(lef1.toInt(),top1.toInt(),rig1.toInt(),bot1.toInt())
            tomd?.draw(canvas)
            jerd?.setBounds(left.toInt(),top.toInt(),right.toInt(),bottom.toInt())
            jerd?.draw(canvas)
            canvas.drawRect(score,scorep)
            val x1 = score.centerX()
            val y1 = score.centerY() - (scoretext.descent() + scoretext.ascent()) / 2
            canvas.drawText(text1.toString(), x1, y1, scoretext)
            canvas.drawRect(finish, finishp)
            val x = finish.centerX()
            val y = finish.centerY() - (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(text, x, y, textPaint)
        }


        var yo = 0
        var toast: Toast = Toast.makeText(
            context,
            "OBSTACLE LIMIT:" + obstacleLimit.toString(),
            Toast.LENGTH_SHORT
        )
        var on = 0

        fun moveCircle() {
            val call = chaseApi.getObstacleLimit()
            call.enqueue(object: Callback<ObstacleLimitResponse> {
                override fun onResponse(
                    call: Call<ObstacleLimitResponse>,
                    response: Response<ObstacleLimitResponse>
                ) {
                    if (response.isSuccessful) {
                        if(yo == 0) {
                            val obstacleLimitResponse = response.body()
                            obstacleLimit = obstacleLimitResponse?.obstacleLimit
                            toast = Toast.makeText(
                                context,
                                "OBSTACLE LIMIT:" + obstacleLimit.toString(),
                                Toast.LENGTH_SHORT
                            )
                            toast.show()
                            yo = 1
                        }
                    } else {
                        Toast.makeText(context, "error in receiving response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ObstacleLimitResponse>, t: Throwable) {
                    Toast.makeText(context, "error in receiving response", Toast.LENGTH_SHORT).show()
                }
            })





            val inflater = LayoutInflater.from(context)
            if(obstacleLimit != null) {

                if (t <= (obstacleLimit!! - 1)) {
                    for (r in r1) {
                        var fi = 0
                        if (left == 159f && top <= r && top >= r - 150f) {
                            t += 1
                            for (i in (1..obstacleLimit!! - 1)) {
                                if (t == i) {
                                    score1 -= 150f
                                    score2 -= 150f
                                    on += 1
                                    toast.cancel()
                                    toast = Toast.makeText(
                                        context,
                                        "Obstacles hit:"+on.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                    toast.show()
                                    bottom = r - 150f
                                    top = r - 279f
                                    fi = 1
                                    break
                                }
                            }
                            if (fi == 1) {
                                break
                            }
                            if (t == obstacleLimit) {
                                top1 = bottom
                                bot1 = top1 + 200f
                                val builder =
                                    AlertDialog.Builder(context, R.style.AlertDialogCustom).create()
                                val view = inflater.inflate(R.layout.activity_finish, null)
                                builder.setView(view)
                                builder.setCanceledOnTouchOutside(false)
                                builder.setCancelable(false)
                                val scoret = view.findViewById<TextView>(R.id.score)
                                scoret.setText("SCORE: " + text1)
                                val again = view.findViewById<Button>(R.id.again)
                                again.setOnClickListener {
                                    val animationZoomIn =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                    again.startAnimation(animationZoomIn)
                                    val animationZoomOut =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                    again.startAnimation(animationZoomOut)
                                    val intent =
                                        Intent(this@MainActivity2, MainActivity2::class.java)
                                    startActivity(intent)
                                }
                                val back = view.findViewById<Button>(R.id.front)
                                back.setOnClickListener {
                                    val animationZoomIn =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                    back.startAnimation(animationZoomIn)
                                    val animationZoomOut =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                    back.startAnimation(animationZoomOut)
                                    if (allEntries.isEmpty()) {
                                        editor.putInt("high", text1)
                                        editor.apply()
                                    } else {
                                        val high = sharedPref.getInt("high", 1)
                                        if (text1 > high) {
                                            editor.putInt("high", text1)
                                            editor.apply()
                                        }
                                    }
                                    if (sharedPref.getInt("high", 1) == text1) {
                                        val intent =
                                            Intent(this@MainActivity2, HighScore::class.java)
                                        startActivity(intent)
                                    } else {
                                        val intent =
                                            Intent(this@MainActivity2, FrontPage::class.java)
                                        startActivity(intent)
                                    }
                                }
                                builder.show()
                            }
                            break
                        }
                    }
                    for (r in r2) {
                        var fi = 0
                        if (left == 472f && top <= r && top >= r - 150f) {
                            t += 1
                            for (i in (1..obstacleLimit!! - 1)) {
                                if (t == i) {
                                    score1 -= 150f
                                    score2 -= 150f
                                    on += 1
                                    toast.cancel()
                                    toast = Toast.makeText(
                                        context,
                                        "Obstacles hit:"+on.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                    toast.show()
                                    bottom = r - 150f
                                    top = r - 279f
                                    fi = 1
                                    break
                                }
                            }
                            if (fi == 1) {
                                break
                            }
                            if (t == obstacleLimit) {
                                top1 = bottom
                                bot1 = top1 + 200f
                                val builder =
                                    AlertDialog.Builder(context, R.style.AlertDialogCustom).create()
                                val view = inflater.inflate(R.layout.activity_finish, null)
                                builder.setView(view)
                                builder.setCanceledOnTouchOutside(false)
                                builder.setCancelable(false)
                                val score = view.findViewById<TextView>(R.id.score)
                                score.setText("SCORE: " + text1)
                                val again = view.findViewById<Button>(R.id.again)
                                again.setOnClickListener {
                                    val animationZoomIn =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                    again.startAnimation(animationZoomIn)
                                    val animationZoomOut =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                    again.startAnimation(animationZoomOut)
                                    val intent =
                                        Intent(this@MainActivity2, MainActivity2::class.java)
                                    startActivity(intent)
                                }
                                val back = view.findViewById<Button>(R.id.front)
                                back.setOnClickListener {
                                    val animationZoomIn =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                    back.startAnimation(animationZoomIn)
                                    val animationZoomOut =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                    back.startAnimation(animationZoomOut)
                                    if (allEntries.isEmpty()) {
                                        editor.putInt("high", text1)
                                        editor.apply()
                                    } else {
                                        val high = sharedPref.getInt("high", 1)
                                        if (text1 > high) {
                                            editor.putInt("high", text1)
                                            editor.apply()
                                        }
                                    }
                                    if (sharedPref.getInt("high", 1) == text1) {
                                        val intent =
                                            Intent(this@MainActivity2, HighScore::class.java)
                                        startActivity(intent)
                                    } else {
                                        val intent =
                                            Intent(this@MainActivity2, FrontPage::class.java)
                                        startActivity(intent)
                                    }
                                }
                                builder.show()
                            }
                            break
                        }
                    }
                    for (r in r3) {
                        var fi = 0
                        if (left == 785f && top <= r && top >= r - 150f) {
                            t += 1
                            for (i in (1..obstacleLimit!! - 1)) {
                                if (t == i) {
                                    score1 -= 150f
                                    score2 -= 150f
                                    on += 1
                                    toast.cancel()
                                    toast = Toast.makeText(
                                        context,
                                        "Obstacles hit:"+on.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                    toast.show()
                                    bottom = r - 150f
                                    top = r - 279f
                                    fi = 1
                                    break
                                }
                            }
                            if (fi == 1) {
                                break
                            }
                            if (t == obstacleLimit) {
                                top1 = bottom
                                bot1 = top1 + 200f
                                val builder =
                                    AlertDialog.Builder(context, R.style.AlertDialogCustom).create()
                                val view = inflater.inflate(R.layout.activity_finish, null)
                                builder.setView(view)
                                builder.setCanceledOnTouchOutside(false)
                                builder.setCancelable(false)
                                val score = view.findViewById<TextView>(R.id.score)
                                score.setText("SCORE: " + text1)
                                val again = view.findViewById<Button>(R.id.again)
                                again.setOnClickListener {
                                    val animationZoomIn =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                    again.startAnimation(animationZoomIn)
                                    val animationZoomOut =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                    again.startAnimation(animationZoomOut)
                                    val intent =
                                        Intent(this@MainActivity2, MainActivity2::class.java)
                                    startActivity(intent)
                                }
                                val back = view.findViewById<Button>(R.id.front)
                                back.setOnClickListener {
                                    val animationZoomIn =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_up)
                                    back.startAnimation(animationZoomIn)
                                    val animationZoomOut =
                                        AnimationUtils.loadAnimation(context, R.anim.scale_down)
                                    back.startAnimation(animationZoomOut)
                                    if (allEntries.isEmpty()) {
                                        editor.putInt("high", text1)
                                        editor.apply()
                                    } else {
                                        val high = sharedPref.getInt("high", 1)
                                        if (text1 > high) {
                                            editor.putInt("high", text1)
                                            editor.apply()
                                        }
                                    }
                                    if (sharedPref.getInt("high", 1) == text1) {
                                        val intent =
                                            Intent(this@MainActivity2, HighScore::class.java)
                                        startActivity(intent)
                                    } else {
                                        val intent =
                                            Intent(this@MainActivity2, FrontPage::class.java)
                                        startActivity(intent)
                                    }
                                }
                                builder.show()
                            }
                            break
                        }
                    }

                }
            }
            if(top <= -69090f && top>=-69345f){
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

            if(obstacleLimit!=null) {
                if (f == 1) {//constant speed
                    if (t == 0 && top > -69090f) {
                        top -= 22f
                        bottom -= 22f
                        top1 -= 22f
                        bot1 -= 22f
                        score1 -= 22f
                        score2 -= 22f
                        text1 += 5
                    }
                    for (i in 1..obstacleLimit!! - 1) {
                        if (t == i && top > -69090f) {//wow this works for stop
                            top -= 22f
                            bottom -= 22f
                            top1 = top + 700f - 100f * i
                            bot1 = top1 + 200f
                            score1 -= 22f
                            score2 -= 22f
                            text1 += 5 - i
                        }
                    }
                    invalidate()
                } else if (f == 0) {//increasing speed
                    var spl = 18f
                    if (t == 0 && top > -23030f) {
                        top -= 18f
                        bottom -= 18f
                        top1 -= 18f
                        bot1 -= 18f
                        score1 -= 18f
                        score2 -= 18f
                        text1 += 3
                        spl = 18f
                    } else if (t == 0 && top > -46060f) {
                        top -= 20f
                        bottom -= 20f
                        top1 -= 20f
                        bot1 -= 20f
                        score1 -= 20f
                        score2 -= 20f
                        text1 += 4
                        spl = 20f
                    } else if (t == 0 && top > -69090f) {
                        top -= 22f
                        bottom -= 22f
                        top1 -= 22f
                        bot1 -= 22f
                        score1 -= 22f
                        score2 -= 22f
                        text1 += 5
                        spl = 22f
                    }
                    for (i in 1..obstacleLimit!! - 1) {
                        if (t == i && top > -69090f) {//wow this works for stop
                            top -= spl
                            bottom -= spl
                            top1 = top + 700f - 100f * i
                            bot1 = top1 + 200f
                            score1 -= spl
                            score2 -= spl
                            text1 += 5 - i
                        }
                    }
                    invalidate()
                }
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