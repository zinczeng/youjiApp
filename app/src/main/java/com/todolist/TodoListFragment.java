package com.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zx.youjiandroid.MainActivity;
import com.example.zx.youjiandroid.R;

import bean.Todo;

public class TodoListFragment extends Fragment {
    private RecyclerView mTodoRecyclerView;
    private TodoLab mTodoLab;
    private Toolbar toolbar;
    private ImageView back_to_user3;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTodoLab = TodoLab.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        setHasOptionsMenu(true);
        back_to_user3= (ImageView) view.findViewById(R.id.back_to_user3);
        back_to_user3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.putExtra("main",3);
                startActivity(intent);
               getActivity().finish();
            }
        });
        toolbar = (Toolbar) view.findViewById(R.id.tb_beiwanglu_add);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mTodoRecyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTodoRecyclerView.setAdapter(new TodoAdapter());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTodoRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_todo_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_todo:
                Todo todo = new Todo("", "");
                mTodoLab.addTodo(todo);
                Intent intentTodoPagerActivity = TodoPagerActivity.createIntent(getContext(), todo.getId());
                startActivity(intentTodoPagerActivity);
                break;
            default:
                break;
        }
        return true;
    }

    private class TodoViewHolder extends RecyclerView.ViewHolder {
        public TextView mTodoTitleTextView;
        public TextView mTodoDateTextView;
        public CheckBox mTodoSolvedCheckBox;
        private Todo mTodo;

        public TodoViewHolder(View itemView) {
            super(itemView);
            mTodoTitleTextView = (TextView) itemView.findViewById(R.id.todo_title_textView);
            mTodoDateTextView = (TextView) itemView.findViewById(R.id.todo_date_textView);
            mTodoSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.todo_solved_checkBox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = TodoPagerActivity.createIntent(getContext(), mTodo.getId());
                    startActivity(intent);
                }
            });
        }

        public void bindTodoViewHolder(Todo todo) {
            mTodo = todo;
            mTodoTitleTextView.setText(todo.getTitle());
            mTodoDateTextView.setText(DateFormat.getDateFormat(getContext()).format(todo.getDate()));
            mTodoSolvedCheckBox.setChecked(todo.isSolved());
        }
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {
        private TodoLab mTodoLab;

        public TodoAdapter() {
            mTodoLab = TodoLab.getInstance(getActivity());
        }

        @Override
        public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.todo_list_item, parent, false);
            return new TodoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TodoViewHolder holder, int position) {
            Todo todo = mTodoLab.getTodoList().get(position);
            holder.bindTodoViewHolder(todo);
        }

        @Override
        public int getItemCount() {
            return mTodoLab.getTodoList().size();
        }
    }

}
