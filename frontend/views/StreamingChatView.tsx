import {useState,useEffect} from "react";
import {MessageList, MessageListItem} from "@hilla/react-components/MessageList";
import {AIChatEndpoint} from "Frontend/generated/endpoints";
import {MessageInput} from "@hilla/react-components/MessageInput";
import { Dialog } from "@hilla/react-components/Dialog.js";
import { UserEndPoint } from "Frontend/generated/endpoints";
import { useNavigate } from "react-router-dom";
import { Button } from "@hilla/react-components/Button.js";
export  function StreamingChatView() {
    const [messages, setMessages] = useState<MessageListItem[]>([]);
    const [account , setAccount] = useState('');
    const [errorOpened , setErrorOpened] =useState(false); 
    const navigate = useNavigate();
    function addMessage(message: MessageListItem) {
        setMessages(messages => [...messages, message]);
    }

    function appendToLastMessage(chunk: string) {
        setMessages(messages => {
            const lastMessage = messages[messages.length - 1];
            lastMessage.text += chunk;
            return [...messages.slice(0, -1), lastMessage];
        });
    }
    useEffect(() => {
        verify();
      }, []);
      async function verify(){
        try{
          const cur = await UserEndPoint.getCurrentUser();
            if (cur)setAccount(cur);
          }catch{
            setErrorOpened(true);
            setTimeout(() => {
                navigate('/')
            }, 1000); 
          }
      }
    async function sendMessage(message: string) {
        addMessage({
            text: message,
            userName: 'You🤌'
        });

        let first = true;
        AIChatEndpoint.chatStream(message).onNext(chunk => {
            if (first && chunk) {
                addMessage({
                    text: chunk,
                    userName: '你的聊天小夥伴🧑‍🤝‍🧑'
                });

                first = false;
            } else {
                appendToLastMessage(chunk);
            }
        });
    }
    function handleDialog(){
        setErrorOpened(false);
        
            navigate('/')
        
      }
    return (
        
        <div className="p-m flex flex-col h-full box-border">
            <MessageList items={messages} className="flex-grow"/>
            <MessageInput onSubmit={e => sendMessage(e.detail.value)}/>
            <div>

    <Dialog
        headerTitle="Login Status"
        opened={errorOpened}
        onOpenedChanged={({ detail }) => setErrorOpened(detail.value)}
        footerRenderer={() => (
            <Button theme="primary" onClick={handleDialog}>Close</Button>
        )}
        >
        <div style={{ textAlign: 'center', minHeight: '100px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column' }}>
            <h2>You haven't Login </h2>
            <h2>Redirect to Rigester Page or enter with bad entry</h2>
        </div>
        </Dialog>
    </div>
        </div>
    );
}
