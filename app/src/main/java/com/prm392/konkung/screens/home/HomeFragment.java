package com.prm392.konkung.screens.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392.konkung.adapters.ProductAdapter;
import com.prm392.konkung.adapters.CategoryAdapter;
import com.prm392.konkung.adapters.BlogAdapter;
import com.prm392.konkung.models.Product;
import com.prm392.konkung.models.Category;
import com.prm392.konkung.models.Blog;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.network.responses.BlogListData;
import com.prm392.konkung.network.responses.CategoryListData;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.Glide;
import com.prm392.konkung.R;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewFeatured;
    private ProductAdapter featuredAdapter;
    private ApiService apiService;
    private List<Product> featuredProducts = new ArrayList<>();
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categories = new ArrayList<>();
    private RecyclerView recyclerViewBlogs;
    private BlogAdapter blogAdapter;
    private List<Blog> blogs = new ArrayList<>();
    private ImageView imgBanner;
    private TextView tvWelcome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgBanner = view.findViewById(R.id.imgBanner);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        // Banner: 1 ảnh tĩnh
        Glide.with(this)
            .load("https://ksetup.vn/wp-content/uploads/2023/02/Mo-dai-ly-sua-1.png")
            .placeholder(R.drawable.ic_milk_logo)
            .error(R.drawable.ic_milk_logo)
            .into(imgBanner);
        // Chào mừng: đã có sẵn text trong layout

        // 3. Danh mục sản phẩm
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        categoryAdapter = new CategoryAdapter(categories, category -> {
            // Chuyển sang ProductListFragment, filter theo category
            com.prm392.konkung.screens.products.ProductListFragment fragment = new com.prm392.konkung.screens.products.ProductListFragment();
            Bundle args = new Bundle();
            args.putInt("categoryId", category.getId());
            args.putString("categoryName", category.getName());
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        });
        LinearLayoutManager catLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategories.setLayoutManager(catLayout);
        recyclerViewCategories.setAdapter(categoryAdapter);
        recyclerViewCategories.setNestedScrollingEnabled(false);
        // Thêm spacing giữa các item danh mục
        int spacing = getResources().getDimensionPixelSize(R.dimen.category_item_spacing);
        recyclerViewCategories.addItemDecoration(new androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect, android.view.View view, androidx.recyclerview.widget.RecyclerView parent, androidx.recyclerview.widget.RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position != 0) {
                    outRect.left = spacing;
                }
            }
        });
        loadCategories();

        // 4. Sản phẩm nổi bật (giữ nguyên logic cũ)
        recyclerViewFeatured = view.findViewById(R.id.recyclerViewFeatured);
        featuredAdapter = new ProductAdapter(featuredProducts, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                com.prm392.konkung.screens.products.ProductDetailFragment detailFragment = com.prm392.konkung.screens.products.ProductDetailFragment.newInstance(product.getId());
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
            @Override
            public void onAddToCartClick(Product product) {
                try {
                    if (product.isAvailable() || product.isPreOrder()) {
                        String userId = com.prm392.konkung.utils.AuthManager.getInstance().getUserId();
                        ApiService apiService = com.prm392.konkung.network.ApiClient.getApiService();
                        com.prm392.konkung.models.AddToCartRequest req = new com.prm392.konkung.models.AddToCartRequest(product.getId(), 1);
                        apiService.addToCart(userId, req).enqueue(new retrofit2.Callback<com.prm392.konkung.network.responses.BaseResponse<com.prm392.konkung.models.CartResponse>>() {
                            @Override
                            public void onResponse(retrofit2.Call<com.prm392.konkung.network.responses.BaseResponse<com.prm392.konkung.models.CartResponse>> call, retrofit2.Response<com.prm392.konkung.network.responses.BaseResponse<com.prm392.konkung.models.CartResponse>> response) {
                                if (response.isSuccessful()) {
                                    String message = product.isPreOrder() ? "Đã thêm vào giỏ hàng (đặt trước)" : "Đã thêm vào giỏ hàng";
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    com.prm392.konkung.screens.main.MainActivity.updateCartBadgeFromFragment(requireActivity());
                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(retrofit2.Call<com.prm392.konkung.network.responses.BaseResponse<com.prm392.konkung.models.CartResponse>> call, Throwable t) {
                                Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Sản phẩm không khả dụng", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFeatured.setLayoutManager(horizontalLayout);
        recyclerViewFeatured.setAdapter(featuredAdapter);
        recyclerViewFeatured.setNestedScrollingEnabled(false);
        apiService = ApiClient.getApiService();
        fetchFeaturedProducts();

        // 5. Tin tức/Bài viết hữu ích
        recyclerViewBlogs = view.findViewById(R.id.recyclerViewBlogs);
        blogAdapter = new BlogAdapter();
        blogAdapter.setOnBlogClickListener(blog -> {
            // Chuyển sang BlogDetailFragment khi click vào bài viết
            com.prm392.konkung.screens.blogs.BlogDetailFragment blogDetailFragment = com.prm392.konkung.screens.blogs.BlogDetailFragment.newInstance(blog.getId());
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, blogDetailFragment)
                .addToBackStack(null)
                .commit();
        });
        LinearLayoutManager blogLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBlogs.setLayoutManager(blogLayout);
        recyclerViewBlogs.setAdapter(blogAdapter);
        recyclerViewBlogs.setNestedScrollingEnabled(false);
        fetchBlogs();

        // 6. Thông tin liên hệ & hỗ trợ
    }

    private List<Category> getDefaultCategories() {
        List<Category> list = new ArrayList<>();
        int icon = R.drawable.ic_milk_logo;
        list.add(new Category(1, "Sữa cho bé 0-12 tháng", icon));
        list.add(new Category(2, "Sữa cho bé 1-3 tuổi", icon));
        list.add(new Category(3, "Sữa cho mẹ bầu", icon));
        list.add(new Category(4, "Sữa nhập khẩu", icon));
        list.add(new Category(5, "Sữa tăng cân, phát triển chiều cao", icon));
        return list;
    }

    private void loadCategories() {
        ApiService apiService = ApiClient.getApiService();
        apiService.getAllCategories(1000).enqueue(new Callback<BaseResponse<CategoryListData>>() {
            @Override
            public void onResponse(Call<BaseResponse<CategoryListData>> call, Response<BaseResponse<CategoryListData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Category> categories = response.body().getData().getItems();
                    categoryAdapter.updateCategories(categories);
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<CategoryListData>> call, Throwable t) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void fetchFeaturedProducts() {
        apiService.getFeaturedProducts().enqueue(new Callback<BaseResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Product>>> call, Response<BaseResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    featuredProducts = response.body().getData();
                    featuredAdapter.updateProducts(featuredProducts);
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<List<Product>>> call, Throwable t) {
                // TODO: Hiển thị thông báo lỗi nếu cần
            }
        });
    }

    private void fetchBlogs() {
        apiService.getAllBlogs().enqueue(new retrofit2.Callback<BaseResponse<BlogListData>>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse<BlogListData>> call, retrofit2.Response<BaseResponse<BlogListData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Blog> allBlogs = response.body().getData().getItems();
                    if (allBlogs != null && !allBlogs.isEmpty()) {
                        List<Blog> topBlogs = allBlogs.size() > 3 ? allBlogs.subList(0, 3) : allBlogs;
                        blogAdapter.setBlogs(topBlogs);
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<BaseResponse<BlogListData>> call, Throwable t) {
                // Không cần báo lỗi lớn, chỉ log nếu cần
            }
        });
    }

    private void setupViews(View view) {
        // TODO: Setup home screen views and functionality
    }
}
