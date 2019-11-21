package com.maxfour.music.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.music.Constants.GITHUB_PROJECT
import com.maxfour.music.R
import com.maxfour.music.activities.base.AbsBaseActivity
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.card_music_info.*
import kotlinx.android.synthetic.main.card_version.*

class AboutActivity : AbsBaseActivity(), View.OnClickListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_about)
		setStatusbarColorAuto()
		setNavigationBarColorPrimary()
		setLightNavigationBar(true)

		setSupportActionBar(toolbar)
		toolbar.apply {
			setTitleTextColor(ATHUtil.resolveColor(this@AboutActivity, R.attr.colorOnPrimary))
			setBackgroundColor(ATHUtil.resolveColor(this@AboutActivity, R.attr.colorPrimary))
			setNavigationOnClickListener { onBackPressed() }
			ToolbarContentTintHelper.colorBackButton(toolbar)
		}
		version.setSummary(getAppVersion())
		setUpView()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressed()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	private fun openUrl(url: String) {
		val i = Intent(Intent.ACTION_VIEW)
		i.data = Uri.parse(url)
		i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		startActivity(i)
	}

	private fun setUpView() {
		appGithub.setOnClickListener(this)
	}

	override fun onClick(view: View) {
		when (view.id) {
			R.id.appGithub -> openUrl(GITHUB_PROJECT)
		}
	}

	private fun getAppVersion(): String {
		return try {
			val packageInfo = packageManager.getPackageInfo(packageName, 0)
			packageInfo.versionName
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
			"0.0.0"
		}
	}
}
