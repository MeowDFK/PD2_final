import {useState} from "react";
import {MessageList, MessageListItem} from "@hilla/react-components/MessageList";
import {AIChatEndpoint} from "Frontend/generated/endpoints";
import {MessageInput} from "@hilla/react-components/MessageInput";
import { Button } from "@hilla/react-components/Button.js";
import { useEffect } from "react";
import { UserEndPoint } from "Frontend/generated/endpoints";
import { useNavigate } from "react-router-dom";
import { Dialog } from "@hilla/react-components/Dialog.js";
export  function AIChatView() {
    const [messages, setMessages] = useState<MessageListItem[]>([]);
    const [account , setAccount] = useState('');
    const [errorOpened , setErrorOpened] =useState(false); 
    const [inputValue, setInputValue] = useState<string>("我的MBTI今日運勢如何");
    const navigate = useNavigate();
    useEffect(() => {
        verify();
      }, []);
      async function verify(){
        try{
          const cur = await UserEndPoint.getCurrentUser();
            if (cur)setAccount(cur);
          }catch{
            setErrorOpened(true);
          }
      }
    async function sendMessage(message : string) {
        setMessages(messages => [...messages, {
            text: message,
            userName: 'You'
        }]);
        const mbti = await UserEndPoint.getUserMBTI(account)
        const response = await AIChatEndpoint.getDivination1(mbti);
        setMessages(messages => [...messages, {
            text: response,
            userName: '魔法水晶🔮'
        }]);
    }
    function handleDialog(){
        setErrorOpened(false);
        
            navigate('/')
        
      }
    return (
      <div className="p-m flex flex-col h-full box-border">
          <MessageList items={messages} className="flex-grow"/>
         <Button theme="primary" onClick={() => {
            setInputValue("我的MBTI今日運勢如何")
            sendMessage(inputValue);
            setInputValue("");
          }}> 我的MBTI今日運勢如何</Button>
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
