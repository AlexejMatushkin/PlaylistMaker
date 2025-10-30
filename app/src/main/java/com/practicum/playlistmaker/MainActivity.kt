package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchButton = findViewById<Button>(R.id.search_button)

        // Способ 1: Анонимный класс
        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(
                    this@MainActivity,
                    SearchActivity::class.java
                )
                startActivity(intent)
            }
        }

        // Способ 2: Лямбда-выражение
        searchButton .setOnClickListener {
            val intent = Intent(
                this,
                SearchActivity::class.java
            )
            startActivity(intent)
        }

        searchButton .setOnClickListener(searchButtonClickListener)

        val mediaButton = findViewById<Button>(R.id.media_button)

        // Способ 1: Анонимный класс
        val mediaButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(
                    this@MainActivity,
                    MediaActivity::class.java
                )
                startActivity(intent)
            }
        }

        // Способ 2: Лямбда-выражение
        mediaButton.setOnClickListener {
            val intent = Intent(
                this,
                MediaActivity::class.java
            )
            startActivity(intent)
        }

        mediaButton.setOnClickListener(mediaButtonClickListener)

        // Третья кнопка - используем лямбда
        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener {
            val intent = Intent(
                this,
                SettingsActivity::class.java
            )
            startActivity(intent)
        }
    }
}
