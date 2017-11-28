export interface SingleMessage {
  userFrom: string;
  date: string;
  content: string;
}

export interface MessagesAggregate {
  messages: Array<SingleMessage>;
  currentPageNumber: number;
  lastPageNumber: number;
}

export interface UserMessagesRequest {
  username: string;
  pageNumber: number;
  messageType: MessageType;
}

export enum MessageType {
  SENT,
  RECEIVED
}

export interface ResponseFromStegHash {
  status: string
}

export interface PublishMessage {
  from: string;
  to: string[];
  message: string;
  hashtags: string[];
  imageOption: ImageOption;
}

export enum ImageOption {
  PROVIDED_BY_USER, //TODO
  SELECTED_BY_USER, //TODO
  RANDOM
}
