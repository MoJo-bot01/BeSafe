package com.example.antigenderbaseviolence.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.VideoLink

class VideoLinkAdapter(private val videoLinks: List<VideoLink>) : RecyclerView.Adapter<VideoLinkAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoLinkAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_webview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoLink = videoLinks[position]
        holder.webView.settings.javaScriptEnabled = true
        val videoId = extractVideoId(videoLink.url)
        val embedUrl = "https://www.youtube.com/embed/$videoId"
        val html = "<iframe width=\"100%\" height=\"100%\" src=\"$embedUrl\" frameborder=\"0\"></iframe>"
        holder.webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

        holder.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: ""

                if (url.contains("youtube.com/watch") || url.contains("youtu.be/")) {
                    // Open YouTube app to handle the video link
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.setPackage("com.google.android.youtube")
                    if (view?.context?.packageManager?.let { intent.resolveActivity(it) } != null) {
                        view?.context?.startActivity(intent)
                        return true
                    }
                }

                // Load all other links in the WebView
                return false
            }
        }

        holder.webView.webChromeClient = WebChromeClient()    }

    override fun getItemCount(): Int {
        return videoLinks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val webView: WebView = itemView.findViewById(R.id.youtubeWebView)
    }

    private fun createIframeHtml(embedUrl: String): String {
        return "<iframe width=\"100%\" height=\"100%\" src=\"$embedUrl\" frameborder=\"0\"></iframe>"
    }

    private fun extractVideoId(link: String): String {
        val regex = "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/watch\\?v=|youtu\\.be\\/)([\\w-]+)(?:&.*)?"
        val matchResult = regex.toRegex().find(link)
        return matchResult?.groupValues?.getOrNull(1) ?: ""
   }
}
