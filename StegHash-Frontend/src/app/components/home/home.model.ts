export interface Message {
  userFrom: string;
  userTo: string[];
  date: string;
  content: string;
}

export interface MessagesAggregate {
  messages: Message[];
  currentPageNumber: number;
  lastPageNumber: number;
}
