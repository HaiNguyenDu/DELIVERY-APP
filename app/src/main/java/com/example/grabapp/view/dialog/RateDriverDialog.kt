package com.example.grabapp.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.data.model.RatingResponse
import com.example.grabapp.databinding.DialogRateDriverBinding

class RateDriverDialog : BaseDialogFragment<DialogRateDriverBinding>() {
    
    companion object {
        private const val ARG_RATING_RESPONSE = "arg_rating_response"
        
        fun newInstance(ratingResponse: RatingResponse): RateDriverDialog {
            return RateDriverDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_RATING_RESPONSE, RatingResponse.toJson(ratingResponse))
                }
            }
        }
    }
    
    private val ratingResponse: RatingResponse? by lazy {
        arguments?.getString(ARG_RATING_RESPONSE)?.let { json ->
            RatingResponse.fromJson(json)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(true)
        }
    }
    
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogRateDriverBinding {
        return DialogRateDriverBinding.inflate(inflater, container, false)
    }

    override fun setUpInit() {
        ratingResponse?.let { rating ->
            setupRatingData(rating)
        }
    }
    
    private fun setupRatingData(rating: RatingResponse) {
        binding.apply {
            // Hiển thị totalReviews
            tvRateCount.text = "Đánh giá ${rating.totalReviews} cuốc xe trước"
            
            // Hiển thị averageRating với 1 số thập phân
            tvTotalStar.text = String.format("%.1f", rating.averageRating)
            
            val totalReviews = rating.totalReviews
            val distribution = rating.ratingDistribution
            
            // Tính progress cho mỗi sao (tỉ lệ phần trăm)
            val calculateProgress = { count: Int ->
                if (totalReviews > 0) {
                    (count * 100 / totalReviews).coerceAtMost(100)
                } else {
                    0
                }
            }
            
            // 5 sao
            tvFiveStarCount.text = distribution.fiveStar.toString()
            pbFiveStar.progress = calculateProgress(distribution.fiveStar)
            
            // 4 sao
            tvFourStarCount.text = distribution.fourStar.toString()
            pbFourStar.progress = calculateProgress(distribution.fourStar)
            
            // 3 sao
            tvThreeStarCount.text = distribution.threeStar.toString()
            pbThreeStar.progress = calculateProgress(distribution.threeStar)
            
            // 2 sao
            tvTwoStarCount.text = distribution.twoStar.toString()
            pbTwoStar.progress = calculateProgress(distribution.twoStar)
            
            // 1 sao
            tvOneStarCount.text = distribution.oneStar.toString()
            pbOneStar.progress = calculateProgress(distribution.oneStar)
        }
    }
}
