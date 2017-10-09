package com.caizhenliang.myemptylayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 各项加载结果的对应显示，适用于ListView及RelativeLayout布局类型；
 *
 * @author SAMCAI
 */
public class EmptyLayout {

    /**
     * Context
     */
    private Context mContext;
    //
    /**
     * 充电器
     */
    private LayoutInflater mInflater;
    //
    /**
     * 加载动画
     */
    private Animation mLoadingAnimation;
    //
    /**
     * ListView用到的布局
     */
    private RelativeLayout mListViewLayout;
    /**
     * RelativeLayout用到的布局
     */
    private FrameLayout mLayout;
    //
    /**
     * 加载时按钮监听器
     */
    private View.OnClickListener mLoadingButtonClickListener;
    /**
     * 空白时按钮监听器
     */
    private View.OnClickListener mEmptyButtonClickListener;
    /**
     * 错误时按钮监听器
     */
    private View.OnClickListener mErrorButtonClickListener;
    //
    /**
     * 主要视图组
     */
    private ViewGroup mViewGroup;
    /**
     * 加载view
     */
    private ViewGroup mLoadingView;
    /**
     * 空白view
     */
    private ViewGroup mEmptyView;
    /**
     * 错误view
     */
    private ViewGroup mErrorView;
    //
    /**
     * 错误提示信息
     */
    private String mErrorMessage = "404 no service";
    /**
     * 空白提示信息
     */
    private String mEmptyMessage = "empty";
    /**
     * 加载提示信息
     */
    private String mLoadingMessage = "loading";
    /**
     * 错误时按钮信息
     */
    private String mErrorButtonMessage = "error";
    /**
     * 空白时按钮信息
     */
    private String mEmptyButtonMessage = "reload";
    /**
     * 加载时按钮信息
     */
    private String mLoadingButtonMessage = "waiting";
    //
    /**
     * 错误信息View资源
     */
    private int mErrorMessageViewId;
    /**
     * 空白信息View资源
     */
    private int mEmptyMessageViewId;
    /**
     * 加载信息View资源
     */
    private int mLoadingMessageViewId;
    /**
     * 加载动画View资源
     */
    private int mLoadingAnimationViewId;
    //
    /**
     * 加载时按钮View资源
     */
    private int mLoadingViewButtonId = R.id.buttonLoading;
    /**
     * 错误时按钮View资源
     */
    private int mErrorViewButtonId = R.id.buttonError;
    /**
     * 空白时按钮View资源
     */
    private int mEmptyViewButtonId = R.id.buttonEmpty;
    //
    /**
     * 显示状态参数
     */
    private int mEmptyType = TYPE_LOADING;
    //
    /**
     * 视图是否已经添加过
     */
    private boolean isViewAdded;
    /**
     * 视图组是否被移除
     */
    private boolean isViewRemoved = false;
    //
    /**
     * 是否显示空白按钮
     */
    private boolean mShowEmptyButton = true;
    /**
     * 是否显示加载按钮
     */
    private boolean mShowLoadingButton = true;
    /**
     * 是否显示错误按钮
     */
    private boolean mShowErrorButton = true;
    //
    /**
     * 显示空白
     */
    public final static int TYPE_EMPTY = 1;
    /**
     * 显示加载中
     */
    public final static int TYPE_LOADING = 2;
    /**
     * 显示发生错误
     */
    public final static int TYPE_ERROR = 3;

    // ---------------------------
    // getters and setters
    // ---------------------------

    /**
     * 获取加载视图；Gets the loading layout
     *
     * @return 加载视图；the loading layout
     */
    public ViewGroup getLoadingView() {
        return mLoadingView;
    }

    /**
     * 设置加载视图；Sets loading layout
     *
     * @param loadingView 加载时显示的视图；the layout to be shown when the list is loading
     */
    public void setLoadingView(ViewGroup loadingView) {
        this.mLoadingView = loadingView;
    }

    /**
     * 设置加载视图资源；Sets loading layout resource
     *
     * @param res 加载时显示的资源；the resource of the layout to be shown when the list
     *            is loading
     */
    public void setLoadingViewRes(int res) {
        this.mLoadingView = (ViewGroup) mInflater.inflate(res, null);
    }

    /**
     * 获取空白结果的视图；Gets the empty layout
     *
     * @return the empty layout
     */
    public ViewGroup getEmptyView() {
        return mEmptyView;
    }

    /**
     * 设置空白结果的视图；Sets empty layout
     *
     * @param emptyView 空白结果的视图；the layout to be shown when no items are available to
     *                  load in the list
     */
    public void setEmptyView(ViewGroup emptyView) {
        this.mEmptyView = emptyView;
    }

    /**
     * 设置空白结果的资源；Sets empty layout resource
     *
     * @param res 空白结果的资源；the resource of the layout to be shown when no items
     *            are available to load in the list
     */
    public void setEmptyViewRes(int res) {
        this.mEmptyView = (ViewGroup) mInflater.inflate(res, null);
    }

    /**
     * 获取错误结果的视图；Gets the error layout
     *
     * @return 错误结果的视图；the error layout
     */
    public ViewGroup getErrorView() {
        return mErrorView;
    }

    /**
     * 设置错误结果的视图；Sets error layout
     *
     * @param errorView 错误结果显示的视图；the layout to be shown when list could not be loaded
     *                  due to some error
     */
    public void setErrorView(ViewGroup errorView) {
        this.mErrorView = errorView;
    }

    /**
     * 设置错误结果视图的资源；Sets error layout resource
     *
     * @param res 错误结果显示的资源；the resource of the layout to be shown when list
     *            could not be loaded due to some error
     */
    public void setErrorViewRes(int res) {
        this.mErrorView = (ViewGroup) mInflater.inflate(res, null);
    }

    /**
     * 获取加载动画；Gets the loading animation
     *
     * @return 加载动画；the loading animation
     */
    public Animation getLoadingAnimation() {
        return mLoadingAnimation;
    }

    /**
     * 设置加载动画；Sets the loading animation
     *
     * @param animation 加载动画；the animation to play when the list is being loaded
     */
    public void setLoadingAnimation(Animation animation) {
        this.mLoadingAnimation = animation;
    }

    /**
     * 设置加载动画的资源；Sets the resource of loading animation
     *
     * @param animationResource 加载动画资源；the animation resource to play when the list is being
     *                          loaded
     */
    public void setLoadingAnimationRes(int animationResource) {
        mLoadingAnimation = AnimationUtils.loadAnimation(mContext, animationResource);
    }

    /**
     * 获取列表View；Gets the list view for which this library is being used
     *
     * @return mViewGroup
     */
    public View getListView() {
        return mViewGroup;
    }

    /**
     * 设置列表View；Sets the list view for which this library is being used
     *
     * @param View 列表
     */
    public void setListView(ViewGroup View) {
        this.mViewGroup = View;
    }

    /**
     * 获取列表状态；Gets the last set state of the list view
     *
     * @return 加载中、空白、错误；loading or empty or error
     */
    public int getEmptyType() {
        return mEmptyType;
    }

    /**
     * 设置列表状态；Sets the state of the empty view of the list view
     *
     * @param emptyType 加载中、空白、错误；loading or empty or error
     */
    public void setEmptyType(int emptyType) {
        this.mEmptyType = emptyType;
        changeEmptyType();
    }

    /**
     * 获取错误提示信息；Gets the message which is shown when the list could not be
     * loaded due to some error
     *
     * @return 错误提示；the error message
     */
    public String getErrorMessage() {
        return mErrorMessage;
    }

    /**
     * 设置错误提示信息；Sets the message to be shown when the list could not be loaded
     * due to some error
     *
     * @param errorMessage  错误提示信息；the error message
     * @param messageViewId 显示提示信息的控件资源；the id of the text view within the error layout
     *                      whose text will be changed into this message
     */
    public void setErrorMessage(String errorMessage, int messageViewId) {
        this.mErrorMessage = errorMessage;
        this.mErrorMessageViewId = messageViewId;
    }

    /**
     * 设置错误提示信息；Sets the message to be shown when the list could not be loaded
     * due to some error
     *
     * @param errorMessage 错误提示信息；the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
    }

    /**
     * 获取错误提示信息；Gets the message which will be shown when the list will be empty
     * for not having any item to display
     *
     * @return 错误提示信息；the message which will be shown when the list will be
     * empty for not having any item to display
     */
    public String getEmptyMessage() {
        return mEmptyMessage;
    }

    /**
     * 设置空白提示信息；Sets the message to be shown when the list will be empty for not
     * having any item to display
     *
     * @param emptyMessage  空白提示信息；the message
     * @param messageViewId 显示提示信息的控件资源；the id of the text view within the empty layout
     *                      whose text will be changed into this message
     */
    public void setEmptyMessage(String emptyMessage, int messageViewId) {
        this.mEmptyMessage = emptyMessage;
        this.mEmptyMessageViewId = messageViewId;
    }

    /**
     * 设置空白提示信息；Sets the message to be shown when the list will be empty for not
     * having any item to display
     *
     * @param emptyMessage 空白提示信息；the message
     */
    public void setEmptyMessage(String emptyMessage) {
        this.mEmptyMessage = emptyMessage;
    }

    /**
     * 获取加载提示信息；Gets the message which will be shown when the list is being
     * loaded
     *
     * @return
     */
    public String getLoadingMessage() {
        return mLoadingMessage;
    }

    /**
     * 设置加载提示信息；Sets the message to be shown when the list is being loaded
     *
     * @param loadingMessage 加载提示信息；the message
     * @param messageViewId  显示加载信息的控件View资源；the id of the text view within the loading
     *                       layout whose text will be changed into this message
     */
    public void setLoadingMessage(String loadingMessage, int messageViewId) {
        this.mLoadingMessage = loadingMessage;
        this.mLoadingMessageViewId = messageViewId;
    }

    /**
     * 设置加载提示信息；Sets the message to be shown when the list is being loaded
     *
     * @param loadingMessage 加载提示信息；the message
     */
    public void setLoadingMessage(String loadingMessage) {
        this.mLoadingMessage = loadingMessage;
    }

    /**
     * 获取加载按钮文字
     *
     * @return 加载按钮文字
     */
    public String getLoadingButtonMessage() {
        return mLoadingButtonMessage;
    }

    /**
     * 设置加载按钮文字
     *
     * @param mLoadingButtonMessage 加载按钮文字
     */
    public void setLoadingButtonMessage(String mLoadingButtonMessage) {
        this.mLoadingButtonMessage = mLoadingButtonMessage;
    }

    /**
     * 获取错误按钮文字
     *
     * @return 错误按钮文字
     */
    public String getErrorButtonMessage() {
        return mErrorButtonMessage;
    }

    /**
     * 设置错误按钮文字
     *
     * @param mErrorButtonMessage 错误按钮文字
     */
    public void setErrorButtonMessage(String mErrorButtonMessage) {
        this.mErrorButtonMessage = mErrorButtonMessage;
    }

    /**
     * 获取空白按钮文字
     *
     * @return 空白按钮文字
     */
    public String getEmptyButtonMessage() {
        return mEmptyButtonMessage;
    }

    /**
     * 设置空白按钮文字
     *
     * @param mEmptyButtonMessage 空白按钮文字
     */
    public void setEmptyButtonMessage(String mEmptyButtonMessage) {
        this.mEmptyButtonMessage = mEmptyButtonMessage;
    }

    /**
     * 获取加载时的动画View资源；Gets the view in the loading layout which will be animated
     * when the list is being loaded
     *
     * @return 加载时的动画View资源the view in the loading layout which will be animated
     * when the list is being loaded
     */
    public int getLoadingAnimationViewId() {
        return mLoadingAnimationViewId;
    }

    /**
     * 设置加载时的动画View资源；Sets the view in the loading layout which will be animated
     * when the list is being loaded
     *
     * @param loadingAnimationViewId the id of the view 加载时的动画View资源；
     */
    public void setLoadingAnimationViewId(int loadingAnimationViewId) {
        this.mLoadingAnimationViewId = loadingAnimationViewId;
    }

    /**
     * 获取加载按钮的监听器；Gets the OnClickListener which perform when LoadingView was
     * click
     *
     * @return 加载按钮的监听器；
     */
    public View.OnClickListener getLoadingButtonClickListener() {
        return mLoadingButtonClickListener;
    }

    /**
     * 设置加载按钮的监听器；Sets the OnClickListener to LoadingView
     *
     * @param loadingButtonClickListener OnClickListener Object 加载按钮的监听器；
     */
    public void setLoadingButtonClickListener(View.OnClickListener loadingButtonClickListener) {
        this.mLoadingButtonClickListener = loadingButtonClickListener;
    }

    /**
     * 获取空白按钮的监听器；Gets the OnClickListener which perform when EmptyView was
     * click
     *
     * @return 空白按钮的监听器；
     */
    public View.OnClickListener getEmptyButtonClickListener() {
        return mEmptyButtonClickListener;
    }

    /**
     * 设置空白按钮的监听器；Sets the OnClickListener to EmptyView
     *
     * @param emptyButtonClickListener OnClickListener Object 空白按钮的监听器；
     */
    public void setEmptyButtonClickListener(View.OnClickListener emptyButtonClickListener) {
        this.mEmptyButtonClickListener = emptyButtonClickListener;
    }

    /**
     * 获取错误按钮的监听器；Gets the OnClickListener which perform when ErrorView was
     * click
     *
     * @return 错误按钮的监听器；
     */
    public View.OnClickListener getErrorButtonClickListener() {
        return mErrorButtonClickListener;
    }

    /**
     * 设置错误按钮的监听器；Sets the OnClickListener to ErrorView
     *
     * @param errorButtonClickListener OnClickListener Object 错误按钮的监听器；
     */
    public void setErrorButtonClickListener(View.OnClickListener errorButtonClickListener) {
        this.mErrorButtonClickListener = errorButtonClickListener;
    }

    /**
     * 空白按钮是否显示；Gets if a button is shown in the empty view
     *
     * @return “ture”，显示；“false”，隐藏；if a button is shown in the empty view
     */
    public boolean isEmptyButtonShown() {
        return mShowEmptyButton;
    }

    /**
     * 设置空白按钮是否显示；Sets if a button will be shown in the empty view
     *
     * @param showEmptyButton will a button be shown in the empty view 是否显示；
     */
    public void setShowEmptyButton(boolean showEmptyButton) {
        this.mShowEmptyButton = showEmptyButton;
    }

    /**
     * 加载按钮是否显示；Gets if a button is shown in the loading view
     *
     * @return “ture”，显示；“false”，隐藏；if a button is shown in the loading view
     */
    public boolean isLoadingButtonShown() {
        return mShowLoadingButton;
    }

    /**
     * 设置加载按钮是否显示；Sets if a button will be shown in the loading view
     *
     * @param showLoadingButton will a button be shown in the loading view 是否显示；
     */
    public void setShowLoadingButton(boolean showLoadingButton) {
        this.mShowLoadingButton = showLoadingButton;
    }

    /**
     * 错误按钮是否显示；Gets if a button is shown in the error view
     *
     * @return “ture”，显示；“false”，隐藏；if a button is shown in the error view
     */
    public boolean isErrorButtonShown() {
        return mShowErrorButton;
    }

    /**
     * 设置错误按钮是否显示；Sets if a button will be shown in the error view
     *
     * @param showErrorButton will a button be shown in the error view 是否显示；
     */
    public void setShowErrorButton(boolean showErrorButton) {
        this.mShowErrorButton = showErrorButton;
    }

    /**
     * 获取加载按钮View的资源；Gets the ID of the button in the loading view
     *
     * @return 加载按钮View的资源；the ID of the button in the loading view
     */
    public int getmLoadingViewButtonId() {
        return mLoadingViewButtonId;
    }

    /**
     * 设置加载按钮View的资源；Sets the ID of the button in the loading view. This ID is
     * required if you want the button the loading view to be click-able.
     *
     * @param loadingViewButtonId the ID of the button in the loading view 加载按钮View的资源;
     */
    public void setLoadingViewButtonId(int loadingViewButtonId) {
        this.mLoadingViewButtonId = loadingViewButtonId;
    }

    /**
     * 获取错误按钮View的资源；Gets the ID of the button in the error view
     *
     * @return 错误按钮View的资源；the ID of the button in the error view
     */
    public int getErrorViewButtonId() {
        return mErrorViewButtonId;
    }

    /**
     * 设置错误按钮View的资源；Sets the ID of the button in the error view. This ID is
     * required if you want the button the error view to be click-able.
     *
     * @param errorViewButtonId the ID of the button in the error view 错误按钮View的资源；
     */
    public void setErrorViewButtonId(int errorViewButtonId) {
        this.mErrorViewButtonId = errorViewButtonId;
    }

    /**
     * 获取空白按钮View的资源；Gets the ID of the button in the empty view
     *
     * @return 空白按钮View的资源；the ID of the button in the empty view
     */
    public int getEmptyViewButtonId() {
        return mEmptyViewButtonId;
    }

    /**
     * 设置空白按钮View的资源；Sets the ID of the button in the empty view. This ID is
     * required if you want the button the empty view to be click-able.
     *
     * @param emptyViewButtonId the ID of the button in the empty view 空白按钮View的资源；
     */
    public void setEmptyViewButtonId(int emptyViewButtonId) {
        this.mEmptyViewButtonId = emptyViewButtonId;
    }

    // ---------------------------
    // private methods
    // ---------------------------

    /**
     * 初始化显示视图，并且根据状态改变显示内容；
     */
    @SuppressWarnings("unchecked")
    private void changeEmptyType() {
        //
        if (isViewRemoved) {
            if (null != mViewGroup) {
                mViewGroup.addView(mLayout, mViewGroup.getChildCount());
                isViewRemoved = false;
            }
        }
        //
        setDefaultValues();
        refreshMessages();

        // 初始插入视图；insert views in the root view
        if (!isViewAdded) {
            // 视图是listView
            if (mViewGroup instanceof ListView) {
                //
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                //
                mListViewLayout = new RelativeLayout(mContext);
                mListViewLayout.setLayoutParams(lp);
                //
                if (mEmptyView != null) {
                    mListViewLayout.addView(mEmptyView);
                }
                if (mLoadingView != null) {
                    mListViewLayout.addView(mLoadingView);
                }
                if (mErrorView != null) {
                    mListViewLayout.addView(mErrorView);
                }
                isViewAdded = true;
                //
                ViewGroup parent = (ViewGroup) mViewGroup.getParent();
                parent.addView(mListViewLayout);
                ((AdapterView<ListAdapter>) mViewGroup).setEmptyView(mListViewLayout);

            } else {
                // 视图是其他
                LayoutParams lp2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
                //
                mLayout = new FrameLayout(mContext);
                mLayout.setLayoutParams(lp2);
                //
                if (mEmptyView != null) {
                    mLayout.addView(mEmptyView);
                }
                if (mLoadingView != null) {
                    mLayout.addView(mLoadingView);
                }
                if (mErrorView != null) {
                    mLayout.addView(mErrorView);
                }
                isViewAdded = true;
                //
                mViewGroup.addView(mLayout, mViewGroup.getChildCount());

            }
        }

        // 根据不同的状态，修改显示状态；change empty type
        if (mViewGroup != null) {
            //
            View loadingAnimationView = null;
            //
            if (mLoadingAnimationViewId > 0) {
                loadingAnimationView = ((Activity) mContext).findViewById(mLoadingAnimationViewId);
            }
            //
            switch (mEmptyType) {
                case TYPE_EMPTY:
                    if (mEmptyView != null) {
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                    if (mErrorView != null) {
                        mErrorView.setVisibility(View.GONE);
                    }
                    if (mLoadingView != null) {
                        mLoadingView.setVisibility(View.GONE);
                        // 取消动画
                        if (loadingAnimationView != null && loadingAnimationView.getAnimation() != null) {
                            loadingAnimationView.getAnimation().cancel();
                        }
                    }

                    break;
                case TYPE_ERROR:
                    if (mEmptyView != null) {
                        mEmptyView.setVisibility(View.GONE);
                    }
                    if (mErrorView != null) {
                        mErrorView.setVisibility(View.VISIBLE);
                    }
                    if (mLoadingView != null) {
                        mLoadingView.setVisibility(View.GONE);
                        // 取消动画
                        if (loadingAnimationView != null && loadingAnimationView.getAnimation() != null)
                            loadingAnimationView.getAnimation().cancel();
                    }

                    break;
                case TYPE_LOADING:
                    if (mEmptyView != null) {
                        mEmptyView.setVisibility(View.GONE);
                    }
                    if (mErrorView != null) {
                        mErrorView.setVisibility(View.GONE);
                    }
                    if (mLoadingView != null) {
                        mLoadingView.setVisibility(View.VISIBLE);
                        // 开启动画
                        if (mLoadingAnimation != null && loadingAnimationView != null) {
                            loadingAnimationView.startAnimation(mLoadingAnimation);

                        } else if (loadingAnimationView != null) {
                            loadingAnimationView.startAnimation(getRotateAnimation());

                        }
                    }

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 刷新各状态提示内容；
     */
    private void refreshMessages() {
        if (mEmptyMessageViewId > 0 && mEmptyMessage != null) {
            ((TextView) mEmptyView.findViewById(mEmptyMessageViewId)).setText(mEmptyMessage);
        }
        if (mLoadingMessageViewId > 0 && mLoadingMessage != null) {
            ((TextView) mLoadingView.findViewById(mLoadingMessageViewId)).setText(mLoadingMessage);
        }
        if (mErrorMessageViewId > 0 && mErrorMessage != null) {
            ((TextView) mErrorView.findViewById(mErrorMessageViewId)).setText(mErrorMessage);
        }
    }

    /**
     * 设置视图各项默认值
     */
    @SuppressLint("InflateParams")
    private void setDefaultValues() {
        //
        if (mEmptyView == null) {
            // 设置布局
            mEmptyView = (ViewGroup) mInflater.inflate(R.layout.view_empty, null);
            // 设置提示信息View资源
            if (!(mEmptyMessageViewId > 0)) {
                //
                mEmptyMessageViewId = R.id.textViewMessage;

            }
            // 是否显示、View资源、设置监听器，三者满足才显示按钮
            if (mShowEmptyButton && mEmptyViewButtonId > 0 && mEmptyButtonClickListener != null) {
                // 找到按钮
                Button emptyViewButton = (Button) mEmptyView.findViewById(mEmptyViewButtonId);
                //
                if (emptyViewButton != null) {
                    // 各项设置
                    emptyViewButton.setText(mEmptyButtonMessage);
                    emptyViewButton.setOnClickListener(mEmptyButtonClickListener);
                    emptyViewButton.setVisibility(View.VISIBLE);

                }
            } else if (mEmptyViewButtonId > 0) {
                //
                Button emptyViewButton = (Button) mEmptyView.findViewById(mEmptyViewButtonId);
                emptyViewButton.setText(mEmptyButtonMessage);
                emptyViewButton.setVisibility(View.GONE);

            }
        }
        if (mLoadingView == null) {
            //
            mLoadingView = (ViewGroup) mInflater.inflate(R.layout.view_loading, null);
            mLoadingAnimationViewId = R.id.imageViewLoading;
            //
            if (!(mLoadingMessageViewId > 0)) {
                //
                mLoadingMessageViewId = R.id.textViewMessage;
            }

            if (mShowLoadingButton && mLoadingViewButtonId > 0 && mLoadingButtonClickListener != null) {
                //
                Button loadingViewButton = (Button) mLoadingView.findViewById(mLoadingViewButtonId);
                //
                if (loadingViewButton != null) {
                    //
                    loadingViewButton.setText(mEmptyButtonMessage);
                    loadingViewButton.setOnClickListener(mLoadingButtonClickListener);
                    loadingViewButton.setVisibility(View.VISIBLE);

                }
            } else if (mLoadingViewButtonId > 0) {
                //
                Button loadingViewButton = (Button) mLoadingView.findViewById(mLoadingViewButtonId);
                loadingViewButton.setText(mEmptyButtonMessage);
                loadingViewButton.setVisibility(View.GONE);

            }
        }
        if (mErrorView == null) {
            //
            mErrorView = (ViewGroup) mInflater.inflate(R.layout.view_error, null);
            //
            if (!(mErrorMessageViewId > 0)) {
                //
                mErrorMessageViewId = R.id.textViewMessage;
            }
            //
            if (mShowErrorButton && mErrorViewButtonId > 0 && mErrorButtonClickListener != null) {
                //
                Button errorViewButton = (Button) mErrorView.findViewById(mErrorViewButtonId);
                //
                if (errorViewButton != null) {
                    //
                    errorViewButton.setText(mEmptyButtonMessage);
                    errorViewButton.setOnClickListener(mErrorButtonClickListener);
                    errorViewButton.setVisibility(View.VISIBLE);

                }
            } else if (mErrorViewButtonId > 0) {
                //
                Button errorViewButton = (Button) mErrorView.findViewById(mErrorViewButtonId);
                errorViewButton.setText(mEmptyButtonMessage);
                errorViewButton.setVisibility(View.GONE);

            }
        }
    }

    /**
     * 加载动画
     *
     * @return 动画
     */
    private static Animation getRotateAnimation() {
        final RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        rotateAnimation.setDuration(1500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        return rotateAnimation;
    }

    // ---------------------------
    // public methods
    // ---------------------------

    /**
     * 构造函数；Constructor
     *
     * @param context the context (preferred context is any activity)
     */
    public EmptyLayout(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 构造函数；Constructor
     *
     * @param context the context (preferred context is any activity)
     * @param View    依附的视图组；the list view for which this library is being used
     */
    public EmptyLayout(Context context, ViewGroup View) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewGroup = View;
    }

    /**
     * 显示空白结果；Shows the empty layout if the list is empty
     */
    public void showEmpty() {
        this.mEmptyType = TYPE_EMPTY;
        changeEmptyType();
    }

    /**
     * 显示加载动画；Shows loading layout if the list is empty
     */
    public void showLoading() {
        this.mEmptyType = TYPE_LOADING;
        changeEmptyType();
    }

    /**
     * 显示错误结果；Shows error layout if the list is empty
     */
    public void showError() {
        this.mEmptyType = TYPE_ERROR;
        changeEmptyType();
    }

    /**
     * 结束显示；
     */
    public void showFinish() {
        mViewGroup.removeViewAt(mViewGroup.getChildCount() - 1);
        isViewRemoved = true;
    }
}
