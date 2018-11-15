package hu.aut.android.todorecyclerviewdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import hu.aut.android.todorecyclerviewdemo.R
import hu.aut.android.todorecyclerviewdemo.ScrollingActivity
import hu.aut.android.todorecyclerviewdemo.data.AppDatabase
import hu.aut.android.todorecyclerviewdemo.data.Todo
import hu.aut.android.todorecyclerviewdemo.touch.TodoTochHelperAdapter
import kotlinx.android.synthetic.main.todo_row.view.*
import java.util.*

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>, TodoTochHelperAdapter {


    var todoItems = mutableListOf<Todo>()

    val context : Context

    constructor(context: Context, todoList: List<Todo>) : super() {
        this.context = context
        this.todoItems.addAll(todoList)
    }

    constructor(context: Context) : super() {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.todo_row, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todoItems[position]

        holder.tvDate.text = todo.createDate
        holder.cbDone.text = todo.todoText
        holder.cbDone.isChecked = todo.done

        holder.btnDelete.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }

        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditTodoDialog(
                todo, holder.adapterPosition
            )
        }

        holder.cbDone.setOnClickListener {
            todo.done=holder.cbDone.isChecked
            Thread {
                AppDatabase.getInstance(
                    context).todoDao().updateTodo(todo)
            }.start()
        }

        holder.itemView.setOnClickListener{
            Toast.makeText(context, "HELLOO", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteTodo(adapterPosition: Int) {
        Thread {
            AppDatabase.getInstance(
                context).todoDao().deleteTodo(todoItems[adapterPosition])

            todoItems.removeAt(adapterPosition)

            (context as ScrollingActivity).runOnUiThread {
                notifyItemRemoved(adapterPosition)
            }
        }.start()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val tvDate = itemView.tvDate
        val cbDone = itemView.cbDone
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
    }


    fun addTodo(todo: Todo) {
        todoItems.add(0, todo)
        //notifyDataSetChanged()
        notifyItemInserted(0)
    }

    override fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(todoItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun updateTodo(item: Todo, editIndex: Int) {
        todoItems[editIndex] = item
        notifyItemChanged(editIndex)
    }

}