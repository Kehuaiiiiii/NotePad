package notepad;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.rtf.RTFEditorKit;

public class Notepad extends JFrame implements ActionListener {

	//窗体和输入区域
	JPanel pl = new JPanel();
	JTextPane textarea = new JTextPane();
               
	private String filename; // 打开的文件名
	String textContent = "";// 编辑框中的上次内容
	
	UndoManager undoManager = new UndoManager();// 撤销管理器
        
        int start = 0;// 查找开始位置
        int end = 0;// 查找结束位置
        
        Font font;// 字体样式

        protected StyleContext m_context;
        protected DefaultStyledDocument m_doc;
        private MutableAttributeSet keyAttr,normalAttr;
        private MutableAttributeSet inputAttributes = new RTFEditorKit().getInputAttributes();
        private String[] keyWord = {
            "int","float","double","void","char",
            "class","public","private","protected","final","new","extends","implements","static",
            "return","if","for","while","break","continue"
        };
        
	public Notepad() {
		initComponment();// 面板初始化
	}

	private void initComponment() {

                m_context = new StyleContext();
                m_doc = new DefaultStyledDocument(m_context);
                textarea.setDocument(m_doc);
                
                // 监听键盘抬起 渲染关键字
                textarea.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent ke) {
                        syntaxParse(2);
                    }
                });
                
                //定义关键字显示属性
                keyAttr = new SimpleAttributeSet();
                StyleConstants.setForeground(keyAttr, Color.blue);

                //定义一般文本显示属性
                normalAttr = new SimpleAttributeSet();
                StyleConstants.setForeground(normalAttr, Color.black);
                
                //------------------------------------------
                
		// 菜单栏
		JMenuBar mb = new JMenuBar();

		// 右键弹出菜单
		final JPopupMenu myPopMenu = new JPopupMenu();
		JMenuItem copy_pop = new JMenuItem("复制");
		JMenuItem cut_pop = new JMenuItem("剪切");
		JMenuItem paste_pop = new JMenuItem("粘贴");
		JMenuItem delete_pop = new JMenuItem("删除");
		
		myPopMenu.add(copy_pop);
                myPopMenu.add(cut_pop);
                myPopMenu.add(paste_pop);
		myPopMenu.add(delete_pop);
		
		// 绑定监听器
                copy_pop.addActionListener(this);
		cut_pop.addActionListener(this);
		paste_pop.addActionListener(this);
		delete_pop.addActionListener(this);
		
		// 菜单
		JMenu file = new JMenu("文件");
		JMenu edit = new JMenu("编辑");
		JMenu about = new JMenu("关于");

		// 子菜单
		JMenuItem new_file = new JMenuItem("新建");
		JMenuItem open = new JMenuItem("打开");
		JMenuItem save = new JMenuItem("保存");
		JMenuItem save_as = new JMenuItem("另存为");
                JMenuItem openBinary = new JMenuItem("打开二进制文件");
                JMenuItem saveBinary = new JMenuItem("保存二进制文件");
		JMenuItem exit = new JMenuItem("退出");

		JMenuItem copy = new JMenuItem("复制");
		JMenuItem cut = new JMenuItem("剪切");
		JMenuItem paste = new JMenuItem("粘贴");
		JMenuItem delete = new JMenuItem("删除");
		JMenuItem search = new JMenuItem("查找和替换");
                JMenuItem time = new JMenuItem("时间/日期");
		
		JMenuItem aboutsoft = new JMenuItem("关于");

		// 绑定监听事件
		new_file.addActionListener(this);
		open.addActionListener(this);
		save.addActionListener(this);
		save_as.addActionListener(this);
                openBinary.addActionListener(this);
                saveBinary.addActionListener(this);
                exit.addActionListener(this);

		copy.addActionListener(this);
		cut.addActionListener(this);
		paste.addActionListener(this);
		delete.addActionListener(this);
		search.addActionListener(this);
                time.addActionListener(this);
		
		aboutsoft.addActionListener(this);
		
		// 将菜单和相应的子菜单添加到菜单栏
		mb.add(file);
		mb.add(edit);
		mb.add(about);

		file.add(open);
		file.add(new_file);
		file.add(save);
		file.add(save_as);
                file.add(openBinary);
                file.add(saveBinary);
		file.add(exit);

		edit.add(copy);
		edit.add(cut);
		edit.add(paste);
		edit.add(delete);
		edit.add(search);
                edit.add(time);
		
		about.add(aboutsoft);

		// 给文本区域添加滚动条
		textarea.add(myPopMenu);
		JScrollPane scrollpane = new JScrollPane(textarea);
		add(scrollpane);
                
		// 主窗口
		setTitle("NJUPT - 记事本");
		setSize(800, 600);
		setLocation(400, 300);
                
                // 设置字体样式
                font = new Font("宋体", Font.BOLD, 16);
                textarea.setFont(font);
                copy_pop.setFont(font);
		cut_pop.setFont(font);
		paste_pop.setFont(font);
		delete_pop.setFont(font);
		file.setFont(font);
		edit.setFont(font);
		about.setFont(font);
		new_file.setFont(font);
		open.setFont(font);
		save.setFont(font);
		save_as.setFont(font);
                openBinary.setFont(font);
                saveBinary.setFont(font);
		exit.setFont(font);
                copy.setFont(font);
		cut.setFont(font);
		paste.setFont(font);
		delete.setFont(font);
		search.setFont(font);
                time.setFont(font);
		aboutsoft.setFont(font);
                
		// 添加菜单栏
		setJMenuBar(mb);
		
		//键盘监听
		textarea.addKeyListener(new KeyAdapter() 
		{
                    @Override
                    public void keyPressed(KeyEvent ke) 
                    {
                        // ctrl+f实现查找功能
                        if ((ke.getKeyCode() == KeyEvent.VK_F) && (ke.isControlDown()))
                        { 
                           findAndReplace();
                        }
                        // ctrl+s实现保存功能
                        if ((ke.getKeyCode() == KeyEvent.VK_S) && (ke.isControlDown()))
                        { 
                           save();
                        }
                    }
		});

		// 鼠标监听
		textarea.addMouseListener(new MouseAdapter() {
                        @Override
			public void mousePressed(MouseEvent e) {
				int mods = e.getModifiers();
				// 鼠标右键
				if ((mods & InputEvent.BUTTON3_MASK) != 0)
                                {
                                    // 弹出菜单
                                    myPopMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
                
                // 窗口监听
		addWindowListener(new WindowAdapter() {
                        @Override
			public void windowClosing(WindowEvent evt) {
                            System.exit(0);
			}
		});
                
	}
        
        
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			if (null != e.getActionCommand())
				switch (e.getActionCommand()) {
                        case "打开":
                            open();
                            break;
                         case "新建":
                            newFile();
                            break;
                        case "保存":
                            save();
                            break;
                        case "另存为":
                            otherSave();
                            break;
                        case "打开二进制文件":
                            openBinary();
                            break;
                        case "保存二进制文件":
                            saveBinary();
                            break;
                        case "退出":
                            quit();
                            break;
                        case "复制":
                            copy();
                            break;
                         case "剪切":
                            cut();
                            break;
                        case "粘贴":
                            paste();
                            break;
                        case "删除":
                            delete();
                            break;
                        case "查找和替换":
                            findAndReplace();
                            break;
                        case "时间/日期":
                            gettime();
                            break;
                        case "关于":
                            about();
                            break;
                        default:
                            break;
                    }
			 
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
        //打开
        private void open() throws FileNotFoundException, IOException {
            if(!textarea.getText().equals(textContent))
            {
                int result = JOptionPane.showConfirmDialog(null, "是否将更改保存到\n"+filename+"?", "记事本", 1);
                switch (result) {
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.YES_OPTION:
                        saveBinary();
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        return;
                    default:
                        return;
                }
            }
            FileDialog fileDialog = new FileDialog(this, "打开文件", FileDialog.LOAD);
            fileDialog.setVisible(true);
            if (fileDialog.getFile() != null) {
                filename = fileDialog.getDirectory() + fileDialog.getFile();// 获得文件名
                // 读取文件
                DataInputStream  dis = new DataInputStream(new FileInputStream(filename));
                String temp = "";
                int ch;
                while((ch = dis.read()) != -1){
                    temp += (char)ch;
                }
                textarea.setText(temp);
                dis.close();
                textContent = textarea.getText();
                setTitle(filename + " - 记事本");
                syntaxParse(1);
            }
            
        }
        
        //打开二进制文件
        private void openBinary() throws FileNotFoundException, IOException {
            if(!textarea.getText().equals(textContent))
            {
                int result = JOptionPane.showConfirmDialog(null, "是否将更改保存到\n"+filename+"?", "记事本", 1);
                switch (result) {
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.YES_OPTION:
                        save();
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        return;
                    default:
                        return;
                }
            }
            FileDialog fileDialog = new FileDialog(this, "打开文件", FileDialog.LOAD);
            fileDialog.setVisible(true);
            if (fileDialog.getFile() != null) {
                filename = fileDialog.getDirectory() + fileDialog.getFile();// 获得文件名
                // 读取文件
                DataInputStream  dis = new DataInputStream(new FileInputStream(filename));
                String temp = "";
                int ch;
                while((ch = dis.read()) != -1){
                    System.out.println((char)(ch+'0'));
                    for(int i=1;i<=8;i++)
                    {
                        temp+=(char)(ch%2+'0');
                        ch=ch>>1;
                    }
                    temp += " ";
                }
                textarea.setText(temp);
                dis.close();
                textContent = textarea.getText();
                setTitle(filename + " - 记事本");
                
            }
        }
        
        //新建
        private void newFile() {
            if(!textarea.getText().equals(textContent))
            {
                int result = JOptionPane.showConfirmDialog(null, "是否将更改保存到\n"+filename+"?", "记事本", 1);
                switch (result) {
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.YES_OPTION:
                        save();
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        return;
                    default:
                        return;
                }
            }
            textarea.setText("");
            textContent = "";
            filename = null;
            setTitle("new - 记事本");
        }
        
        //保存二进制文件
	private void saveBinary() {
		if (filename != null) 
		{
			try {
                                FileOutputStream fos = new FileOutputStream(filename);
                                DataOutputStream dos = new DataOutputStream(fos);
                                String string = textarea.getText();
                                int temp,len=string.length();
                                for(int i=0;i<len;i++)
                                {
                                    if((i+1)%9 == 0)
                                    {
                                        temp = 0;
                                        for(int j=1;j<=8;j++)
                                        {
                                            //System.out.println(string.charAt(i-j)-'0');
                                            temp = temp*2 + (string.charAt(i-j)-'0');
                                        }
                                        //System.out.println("temp="+temp);
                                        dos.write(temp);
                                    }
                                }
                                textContent = textarea.getText();
				fos.close();
				dos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			otherSave();
		}
		
	}
        
	//保存
	private void save() {
		if (filename != null) 
		{
			try {
				File file = new File(filename);
				FileWriter file_writer = new FileWriter(file);
				//将文件输出流包装进缓冲区
				BufferedWriter bw = new BufferedWriter(file_writer);
				PrintWriter pw = new PrintWriter(bw);
                                    
				pw.print(textarea.getText());
				textContent = textarea.getText();
				pw.close();
				bw.close();
				file_writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			otherSave();
		}
		
	}
	
	//另存为
	private void otherSave() 
	{
		FileDialog fileDialog = new FileDialog(this, "另存为", FileDialog.SAVE);
		fileDialog.setVisible(true);
		if (fileDialog.getFile() != null) {
			// 写入文件
			FileWriter fw;
			try {
                        filename = fileDialog.getDirectory() + fileDialog.getFile();
			fw = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.print(textarea.getText());
			textContent = textarea.getText();
			pw.close();
			bw.close();
			fw.close();
                        setTitle(filename + " - 记事本");
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
        
        //退出
        private void quit(){
            if(!textarea.getText().equals(textContent))
            {
                int result = JOptionPane.showConfirmDialog(null, "是否将更改保存到\n"+filename+"?", "记事本", 1);
                switch (result) {
                    case JOptionPane.NO_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.YES_OPTION:
                        save();
                        System.exit(0);
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        break;
                    default:
                        break;
                }
            }
            else {
                System.exit(0);
            }
        }
	
        //复制
	private void copy() {
		if (textarea.getSelectedText() == null) {
                    return ;
		}
                // 构造系统剪切板
		Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
                // 获取选取内容
		StringSelection stringSelection = new StringSelection(textarea.getSelectedText());
		clipBoard.setContents(stringSelection, null);
	}
        
	//剪切
	private void cut() {
		copy();
		delete();
	}
        
	//粘贴
	private void paste() throws UnsupportedFlavorException, IOException {
		String content_copy = "";
		// 构造系统剪切板
		Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 获取剪切板内容
		Transferable content = clipBoard.getContents(null);

		if (content != null) {
			// 检查是否是文本类型
			if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				content_copy = (String) content.getTransferData(DataFlavor.stringFlavor);
                                textarea.replaceSelection(content_copy);
                                syntaxParse(1);
			}
		} 
	}

	//删除
	private void delete() {
		if (textarea.getSelectedText() == null) {
                    return ;
                    //JOptionPane.showMessageDialog(null, "你没有选中任何文字！", "记事本", JOptionPane.WARNING_MESSAGE);
		}
		textarea.replaceSelection("");
	}
        
        //查找替换
        private void findAndReplace(){
            // 查找对话框
            JDialog search = new JDialog(this, "查找和替换");
            search.setSize(400, 150);
            search.setLocation(450, 350);
            search.setVisible(true);
            search.setResizable(false);// 固定大小
            JLabel label_1 = new JLabel("查找的内容：");
            JLabel label_2 = new JLabel("替换的内容：");
            final JTextField textField_1 = new JTextField(5);
            final JTextField textField_2 = new JTextField(5);
            JButton buttonFind = new JButton("查找");
            JButton buttonChange = new JButton("替换");
            JPanel panel = new JPanel(new GridLayout(2, 3));
            panel.add(label_1);
            panel.add(textField_1);
            panel.add(buttonFind);
            panel.add(label_2);
            panel.add(textField_2);
            panel.add(buttonChange);
            label_1.setFont(font);
            label_2.setFont(font);
            textField_1.setFont(font);
            textField_2.setFont(font);
            buttonFind.setFont(font);
            buttonChange.setFont(font);
            search.add(panel);
            // 为查找下一个 按钮绑定监听事件
            buttonFind.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    String findText = textField_1.getText();// 查找的字符串

                    String textArea = textarea.getText();// 当前文本框的内容
                    textArea = textArea.replaceAll("\r", "");// 去掉\r
                    start = textArea.indexOf(findText, textarea.getSelectionEnd());

                    // 没有找到
                    if (start == -1)
                    {
                        start = textArea.indexOf(findText, 0);
                        if(start == -1)
                            JOptionPane.showMessageDialog(null, "找不到\""+findText+"\"", "记事本", JOptionPane.WARNING_MESSAGE);
                        else {
                            end = start + findText.length();
                            textarea.select(start, end);
                        }
                    } else 
                    {         
                        end = start + findText.length();
                        textarea.select(start, end);
                    }

                }
            });
            // 为替换按钮绑定监听事件
            buttonChange.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    String findText = textField_1.getText();// 查找的字符串
                    String changeText = textField_2.getText();// 替换的字符串
                    String textArea = textarea.getText();// 当前文本框的内容
                    textArea = textArea.replaceAll("\r", "");// 去掉\r
                    start = textArea.indexOf(findText, textarea.getSelectionEnd());

                    // 没有找到
                    if (start == -1)
                    {
                        JOptionPane.showMessageDialog(null, "找不到\""+findText+"\"", "记事本", JOptionPane.WARNING_MESSAGE);
                    } else 
                    {
                        end = start + findText.length();
                        textarea.select(start, end);
                        textarea.replaceSelection(changeText);
                        end = start + changeText.length();
                        textarea.select(start, end);
                        syntaxParse(1);
                    }        
                }
            });
        }
        
        //时间/日期
        private void gettime() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间
            //System.out.println(date);
            textarea.replaceSelection(date);
        }
        
        //关于
        private void about() {
            JOptionPane.showMessageDialog(null,"作者：叶科淮、王凯锐、王仁杰","njupt-java程序设计作业",JOptionPane. INFORMATION_MESSAGE);
        }
        
        //语法高亮
        public void syntaxParse(int op) { // 1为全局渲染 2为局部渲染
            try {
            String s;
            Element root,para;
            int start,end,cursorPos,line;
            if(op == 1) {
                s = textarea.getText();
                start = 0;
                s = s.replaceAll("\t|\n", " ");
                s = s.replaceAll("\r", "");
            }
            else {
                s = null;
                root = m_doc.getDefaultRootElement();
                cursorPos = textarea.getCaretPosition();
                line = root.getElementIndex(cursorPos);
                para  = root.getElement(line);
                start = para.getStartOffset();
                end = para.getEndOffset() - 1;
                s = m_doc.getText(start, end - start);
                s = s.replaceAll("\t", " ");
            }

            //System.out.println("s = " + s);
            int i = 0;
            int xStart = 0;
            
            //分析关键字
            m_doc.setCharacterAttributes(start, s.length(),normalAttr, false);
            MyStringTokenizer st = new MyStringTokenizer(s);
            while( st.hasMoreTokens()) {
            s = st.nextToken();
            if ( s == null) return;
            for (i = 0; i < keyWord.length; i++ ) {
            if (s.equals(keyWord[i])) 
                break;
            }
            if ( i >= keyWord.length ) 
                continue;

            xStart = st.getCurrPosition();

            //设置关键字显示属性
            m_doc.setCharacterAttributes(start+xStart, s.length(),keyAttr, false);
            }
            inputAttributes.addAttributes(normalAttr);
            } catch (Exception ex) {
            ex.printStackTrace();
            }
}
        
        public static void main(String[] args) {
		new Notepad().setVisible(true);
	}
        
}

/*在分析字符串的同时，记录每个token所在的位置
*
*/
class MyStringTokenizer extends StringTokenizer{
    String sval = " ";
    String oldStr,str;
    int m_currPosition = 0,m_beginPosition=0;
    MyStringTokenizer(String str) {
        super(str," ");
        this.oldStr = str;
        this.str = str;
    }

    @Override
    public String nextToken() {
        try {
        String s = super.nextToken();
        int pos = -1;
        if (oldStr.equals(s)) {
            return s;
        }
        pos = str.indexOf(s + sval);
        if ( pos == -1) {
            pos = str.indexOf(sval + s);
            if ( pos == -1)
                return null;
            else pos += 1;
        }

        int xBegin = pos + s.length();
        str = str.substring(xBegin);

        m_currPosition = m_beginPosition + pos;
        m_beginPosition = m_beginPosition + xBegin;
        return s;
        } catch (java.util.NoSuchElementException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //返回token在字符串中的位置
    public int getCurrPosition() {
        return m_currPosition;
    }
}