package com.example.gptapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gptapi.adapter.MessageAdapter
import com.example.gptapi.api.ApiUtilities
import com.example.gptapi.databinding.ActivityChatBinding
import com.example.gptapi.databinding.ActivityImageGenerateBinding
import com.example.gptapi.models.request.ChatRequest
import com.example.gptapi.models.request.ImageGenerateRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import java.lang.Exception

class ImageGenerateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageGenerateBinding

    var list = ArrayList<MessageModel>()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var adapter: MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageGenerateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            finish()
        }

        mLayoutManager = LinearLayoutManager(this@ImageGenerateActivity)
        mLayoutManager.stackFromEnd = true
        adapter = MessageAdapter(list)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = mLayoutManager

        binding.sendbtn.setOnClickListener {
            if (binding.userMsg.text!!.isEmpty()) {
                Toast.makeText(
                    this@ImageGenerateActivity,
                    "Please input message",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                callApi()
            }
        }
    }

    private fun callApi() {

        var query: String = binding.userMsg.text.toString()
        binding.userMsg.text?.clear()
        list.add(MessageModel(true, false, query))
        adapter.notifyItemInserted(list.size - 1)

        binding.recyclerView.recycledViewPool.clear()
        binding.recyclerView.smoothScrollToPosition(list.size - 1)

        val apiInterface = ApiUtilities.getApiInterface()

        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            Gson().toJson(
                ImageGenerateRequest(
                    1,
                    query,
                    "1024x1024"
                )
            )
        )


        val contentType = "application/json"
        val authorization = "Bearer ${Utils.API_KEY}"

        lifecycleScope.launch(Dispatchers.IO) {

            try {
                val response = apiInterface.generateImage(
                    contentType, authorization, requestBody
                )
                val textResponse = response.data.first().url

                list.add(MessageModel(false, true, textResponse))
                withContext(Dispatchers.Main) {
                    adapter.notifyItemInserted(list.size - 1)

                    binding.recyclerView.recycledViewPool.clear()
                    binding.recyclerView.smoothScrollToPosition(list.size - 1)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ImageGenerateActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
}
