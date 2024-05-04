package com.shiki.kisanfintech.ui.languagepriority

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.shiki.kisanfintech.R
import com.shiki.kisanfintech.databinding.ActivityLanguageBinding
import com.shiki.kisanfintech.interfaces.LanguageManager
import com.shiki.kisanfintech.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LanguageActivity : AppCompatActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    private var _binding:ActivityLanguageBinding ?= null
    private val binding get()=_binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClickListner()

        setOnCheckedChangeListener()
    }

    private fun onClickListner() {
        binding.btnContinue.setOnClickListener(View.OnClickListener {
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
        })
    }

    private fun setOnCheckedChangeListener() {
        binding.rdGroup.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.rb_lang_english == checkedId) changeLanguage("en") else changeLanguage("hi")
        }
    }

    private fun changeLanguage(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }
}