package com.example.gptapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gptapi.adapter.MessageAdapter
import com.example.gptapi.api.ApiUtilities
import com.example.gptapi.databinding.ActivityChatBinding
import com.example.gptapi.models.request.ChatRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody

import java.lang.Exception

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding

    var list = ArrayList<MessageModel>()
    private lateinit var mLayoutManager : LinearLayoutManager
    private lateinit var adapter : MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener{
            finish()
        }

        mLayoutManager = LinearLayoutManager(this@ChatActivity)
        mLayoutManager.stackFromEnd = true
        adapter = MessageAdapter(list)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = mLayoutManager

        binding.sendbtn.setOnClickListener {
            if(binding.userMsg.text!!.isEmpty()){
                Toast.makeText(this@ChatActivity,"Please input message",Toast.LENGTH_SHORT).show()
            }else{
                callApi()
            }
        }
    }

    private fun callApi(){

        var query:String  = binding.userMsg.text.toString()
        binding.userMsg.text?.clear()
        list.add(MessageModel(true,false,query))
        adapter.notifyItemInserted(list.size - 1)

        binding.recyclerView.recycledViewPool.clear()
        binding.recyclerView.smoothScrollToPosition(list.size - 1)

        val apiInterface = ApiUtilities.getApiInterface()

        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            Gson().toJson(
                ChatRequest(
                250,
                    "text-davinci-003",
                    query,
                    0.7
                    )
            )
        )


        val contentType = "application/json"
        val authorization = "Bearer ${Utils.API_KEY}"

        lifecycleScope.launch(Dispatchers.IO){

            try {
                val response = apiInterface.getChat(
                    contentType, authorization, requestBody
                )
                val textResponse = response.choices.first().text

                list.add(MessageModel(false, false, textResponse))
                withContext(Dispatchers.Main){
                    adapter.notifyItemInserted(list.size - 1)

                    binding.recyclerView.recycledViewPool.clear()
                    binding.recyclerView.smoothScrollToPosition(list.size - 1)
                }

            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@ChatActivity,e.message,Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
}