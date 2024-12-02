package com.example.froyo;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LenientTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        // List<List<Integer>> 타입에 대해서만 어댑터를 제공
        if (!List.class.isAssignableFrom(type.getRawType()) || !type.getType().toString().contains("List<List<Integer>>")) {
            return null;
        }

        // TypeAdapter 생성
        return (TypeAdapter<T>) new TypeAdapter<List<List<Integer>>>() {
            @Override
            public void write(JsonWriter out, List<List<Integer>> value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.beginArray();
                for (List<Integer> innerList : value) {
                    out.beginArray();
                    for (Integer val : innerList) {
                        out.value(val);
                    }
                    out.endArray();
                }
                out.endArray();
            }

            @Override
            public List<List<Integer>> read(JsonReader in) throws IOException {
                List<List<Integer>> list = new ArrayList<>();
                if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    in.beginArray();
                    while (in.hasNext()) {
                        List<Integer> innerList = new ArrayList<>();
                        if (in.peek() == JsonToken.BEGIN_ARRAY) {
                            in.beginArray();
                            while (in.hasNext()) {
                                if (in.peek() == JsonToken.NUMBER) {
                                    innerList.add(in.nextInt());
                                } else {
                                    in.skipValue(); // 예상하지 못한 값이 있을 경우 건너뜀
                                }
                            }
                            in.endArray();
                        }
                        list.add(innerList);
                    }
                    in.endArray();
                } else {
                    in.skipValue();
                }
                return list;
            }
        }.nullSafe();
    }
}
