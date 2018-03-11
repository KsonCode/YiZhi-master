package com.zyw.horrarndoo.yizhi.ui.fragment.home.child.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zyw.horrarndoo.sdk.base.BasePresenter;
import com.zyw.horrarndoo.sdk.base.fragment.BaseRecycleFragment;
import com.zyw.horrarndoo.sdk.utils.ResourcesUtils;
import com.zyw.horrarndoo.yizhi.R;
import com.zyw.horrarndoo.yizhi.adapter.ZhihuAdapter;
import com.zyw.horrarndoo.yizhi.contract.home.tabs.ZhihuContract;
import com.zyw.horrarndoo.yizhi.model.bean.zhihu.ZhihuDailyItemBean;
import com.zyw.horrarndoo.yizhi.presenter.home.tabs.ZhihuPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Horrarndoo on 2017/9/11.
 * <p>
 */

public class ZhihuFragment extends BaseRecycleFragment<ZhihuContract.ZhihuPresenter,
        ZhihuContract.IZhihuModel> implements ZhihuContract.IZhihuView, BaseQuickAdapter
        .RequestLoadMoreListener,OnBannerListener,SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_zhihu)
    RecyclerView rvZhihu;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private Banner banner;

    private View headView;

    private ArrayList<String> list_path;
    private ArrayList<String> list_title;

    private ZhihuAdapter mZhihuAdapter;

    public static ZhihuFragment newInstance() {
        Bundle args = new Bundle();
        ZhihuFragment fragment = new ZhihuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_zhihu;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
        //初始化一个空list的adapter，网络错误时使用，第一次加载到数据时重新初始化adapter并绑定recycleview
        mZhihuAdapter = new ZhihuAdapter(R.layout.item_recycle_home);
        rvZhihu.setAdapter(mZhihuAdapter);
        rvZhihu.setLayoutManager(new LinearLayoutManager(mActivity));
        refreshLayout.setOnRefreshListener(this);

    }

    /**
     * 初始化轮播图测试
     */
    private void initBanner() {

        rvZhihu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rvZhihu.requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        //放图片地址的集合
        list_path = new ArrayList<>();
        //放标题的集合
        list_title = new ArrayList<>();

        list_path.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg");
        list_path.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg");
        list_path.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg");
        list_path.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic2e7vsaj30ci08cglz.jpg");
        list_title.add("好好学习");
        list_title.add("天天向上");
        list_title.add("热爱劳动");
        list_title.add("不搞对象");
        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器，图片加载器在下方
        banner.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        banner.setImages(list_path);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        banner.setBannerAnimation(Transformer.Default);
        //设置轮播图的标题集合
        banner.setBannerTitles(list_title);
        //设置轮播间隔时间
        banner.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        banner.isAutoPlay(true);
        //设置指示器的位置，小点点，左中右。
        banner.setIndicatorGravity(BannerConfig.CENTER)
                //以上内容都可写成链式布局，这是轮播图的监听。比较重要。方法在下面。
                .setOnBannerListener(this)
                //必须最后调用的方法，启动轮播图。
                .start();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mPresenter.loadLatestList();//第一次显示时请求最新的list
    }

    @NonNull
    @Override
    public BasePresenter initPresenter() {
        return ZhihuPresenter.newInstance();
    }

    @Override
    public void updateContentList(List<ZhihuDailyItemBean> list) {
        //        Logger.e(list.toString());
        if (mZhihuAdapter.getData().size() == 0) {
            initRecycleView(list);
        } else {
            mZhihuAdapter.addData(list);
        }
    }

    @Override
    public void itemNotifyChanged(int position) {
        mZhihuAdapter.notifyItemChanged(position);
    }

    @Override
    public void showNetworkError() {
        //Logger.e("Network error.");
        mZhihuAdapter.setEmptyView(errorView);
    }

    @Override
    public void showLoadMoreError() {
        //Logger.e("load more error.");
        mZhihuAdapter.loadMoreFail();
    }

    @Override
    public void showNoMoreData() {
        mZhihuAdapter.loadMoreEnd(false);
    }

    @Override
    public void onLoadMoreRequested() {
        mZhihuAdapter.loadMoreComplete();
        mPresenter.loadMoreList();
    }

    private void initRecycleView(List<ZhihuDailyItemBean> list) {
        mZhihuAdapter = new ZhihuAdapter(R.layout.item_recycle_home, list);
        mZhihuAdapter.setOnLoadMoreListener(this, rvZhihu);
        mZhihuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPresenter.onItemClick(position, (ZhihuDailyItemBean) adapter.getItem(position));
            }
        });
        initHeadView();
        mZhihuAdapter.addHeaderView(headView);
        rvZhihu.setAdapter(mZhihuAdapter);
    }

    @Override
    protected void onErrorViewClick(View view) {
        mPresenter.loadLatestList();
    }

    @Override
    protected void showLoading() {
        mZhihuAdapter.setEmptyView(loadingView);
    }



    @Override
    public void OnBannerClick(int position) {

        Toast.makeText(mContext, "position:"+position, Toast.LENGTH_SHORT).show();

    }

    private void initHeadView() {
        if (headView == null) {
            headView = ResourcesUtils.inflate(R.layout.fragment_top_banner_layout);
            banner = (Banner) headView.findViewById(R.id.banner);
            initBanner();
        }

    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
    }

    //自定义的图片加载器
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((String) path).into(imageView);
        }
    }
}
