import React, { useEffect, useState } from 'react';
import { MessageList } from '@hilla/react-components/MessageList.js';
import { MessageInput } from '@hilla/react-components/MessageInput.js';
import { useParams } from 'react-router-dom';
import { ChatEndPoint } from 'Frontend/generated/endpoints';
import  MessageRecord from 'Frontend/generated/com/example/application/data/service/ChatService/MessageRecord';
import { TextFieldChangeEvent } from '@vaadin/text-field/src/vaadin-text-field.js';
import { MessageInputSubmitEvent } from '@vaadin/message-input/src/vaadin-message-input.js';

export function ChatView() {
  const { roomId , account} = useParams<Record<string, string>>();
  const [messages, setMessages] = useState< MessageRecord[]>([]);
  const [message, setMessage] = useState('');
  
  console.log("useParams:", useParams<Record<string, string>>());
  useEffect(() => {
    if (roomId) {
      const subscription = ChatEndPoint.joinChatRoom(Number(roomId)).onNext(msg => {
        setMessages((prevState) => [...prevState, msg]);
      });
      
      return () => subscription.cancel();
    }
    // In case roomId is undefined, return an empty cleanup function
    return () => {};
  }, [roomId]);

  function sendMessage(message: string) {
    if (roomId) {
      ChatEndPoint.sendMessage(Number(roomId), String(account), message);
      setMessage('');
    }
}
function handleInput(e: TextFieldChangeEvent) {
    setMessage(e.target.value);
  }


  const messageListItems = messages.map((msg) => ({
    text: msg.content,
    userName: msg.username?msg.username:'unknown',
    time: msg.timestamp ? msg.timestamp.toString() : 'Unknown time', // Ensure this is a string
  }));


  return (
    <div className="flex flex-col p-m box-border h-full">
      <MessageList items={messageListItems} className="flex-grow" />
      <MessageInput onSubmit={e=>sendMessage(e.detail.value)} />
    </div>
  );

};

